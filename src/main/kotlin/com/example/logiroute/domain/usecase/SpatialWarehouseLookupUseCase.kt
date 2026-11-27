package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Warehouse
import kotlin.math.max
import kotlin.math.min

data class SpatialLookupResult(
    val targetGeohash: String,
    val neighborGeohashes: Map<NeighborDirection, String>,
    val nearbyWarehouses: List<Warehouse>
)

enum class NeighborDirection(val latStep: Int, val lonStep: Int) {
    NORTH(1, 0),
    SOUTH(-1, 0),
    EAST(0, 1),
    WEST(0, -1),
    NORTH_EAST(1, 1),
    NORTH_WEST(1, -1),
    SOUTH_EAST(-1, 1),
    SOUTH_WEST(-1, -1)
}

class SpatialWarehouseLookupUseCase {

    companion object {
        private const val MIN_LAT = -90.0
        private const val MAX_LAT = 90.0
        private const val MIN_LON = -180.0
        private const val MAX_LON = 180.0
        private const val BITS_PER_CHAR = 5
        private const val BASE32_ALPHABET = "0123456789bcdefghjkmnpqrstuvwxyz"
    }

    /**
     * نقطة الدخول المختصرة:
     * 1) تشفير الموقع المستهدف إلى Geohash حسب الدقة المطلوبة
     * 2) حساب المربعات المجاورة الـ 8
     * 3) فهرسة المستودعات حسب الـ prefix والبحث السريع عبر البادئات
     */
    operator fun invoke(
        latitude: Double,
        longitude: Double,
        warehouses: List<Warehouse>,
        precision: Int = 5,
        includeNeighbors: Boolean = true
    ): SpatialLookupResult {
        validateInputs(latitude, longitude, precision)

        val targetHash = encodeGeohash(latitude, longitude, precision)
        val neighbors = if (includeNeighbors) computeEightNeighbors(targetHash) else emptyMap()
        val searchPrefixes = buildSearchPrefixes(targetHash, neighbors)
        val prefixIndex = indexWarehousesByPrefix(warehouses, precision)
        val matchedWarehouses = queryWarehousesByPrefixes(prefixIndex, searchPrefixes)

        return SpatialLookupResult(
            targetGeohash = targetHash,
            neighborGeohashes = neighbors,
            nearbyWarehouses = matchedWarehouses
        )
    }

    // 1. Binary Interleaving + 2. Base-32 Encoding + 3. Precision Levels
    private fun encodeGeohash(latitude: Double, longitude: Double, precision: Int): String {
        var latLow = MIN_LAT
        var latHigh = MAX_LAT
        var lonLow = MIN_LON
        var lonHigh = MAX_LON

        var isLongitudeTurn = true // even bits -> longitude, odd bits -> latitude
        var bitCounter = 0
        var fiveBitValue = 0
        val result = StringBuilder(precision)

        while (result.length < precision) {
            val bit: Int
            if (isLongitudeTurn) {
                val mid = (lonLow + lonHigh) / 2.0
                if (longitude >= mid) {
                    bit = 1
                    lonLow = mid
                } else {
                    bit = 0
                    lonHigh = mid
                }
            } else {
                val mid = (latLow + latHigh) / 2.0
                if (latitude >= mid) {
                    bit = 1
                    latLow = mid
                } else {
                    bit = 0
                    latHigh = mid
                }
            }

            fiveBitValue = (fiveBitValue shl 1) or bit
            bitCounter++
            isLongitudeTurn = !isLongitudeTurn

            if (bitCounter == BITS_PER_CHAR) {
                result.append(BASE32_ALPHABET[fiveBitValue])
                bitCounter = 0
                fiveBitValue = 0
            }
        }

        return result.toString()
    }

    // فك حدود المربع لحساب المربعات المجاورة بدقة
    private fun decodeBoundingBox(geohash: String): BoundingBox {
        var latLow = MIN_LAT
        var latHigh = MAX_LAT
        var lonLow = MIN_LON
        var lonHigh = MAX_LON
        var isLongitudeTurn = true

        for (ch in geohash.lowercase()) {
            val charIndex = BASE32_ALPHABET.indexOf(ch)
            require(charIndex != -1) { "Invalid Geohash character: $ch" }

            for (mask in intArrayOf(16, 8, 4, 2, 1)) {
                val bit = if ((charIndex and mask) != 0) 1 else 0
                if (isLongitudeTurn) {
                    val mid = (lonLow + lonHigh) / 2.0
                    if (bit == 1) lonLow = mid else lonHigh = mid
                } else {
                    val mid = (latLow + latHigh) / 2.0
                    if (bit == 1) latLow = mid else latHigh = mid
                }
                isLongitudeTurn = !isLongitudeTurn
            }
        }

        return BoundingBox(latLow, latHigh, lonLow, lonHigh)
    }

    // 4. Neighboring Bounding Boxes (8 adjacent boxes)
    private fun computeEightNeighbors(geohash: String): Map<NeighborDirection, String> {
        val box = decodeBoundingBox(geohash)
        val centerLat = (box.minLat + box.maxLat) / 2.0
        val centerLon = (box.minLon + box.maxLon) / 2.0
        val latSpan = box.maxLat - box.minLat
        val lonSpan = box.maxLon - box.minLon

        return NeighborDirection.entries.associateWith { dir ->
            val nextLat = clampLatitude(centerLat + dir.latStep * latSpan)
            val nextLon = wrapLongitude(centerLon + dir.lonStep * lonSpan)
            encodeGeohash(nextLat, nextLon, geohash.length)
        }
    }

    // 5. Spatial Lookup Query (Indexing by prefix & prefix-based lookup)
    private fun buildSearchPrefixes(
        targetHash: String,
        neighbors: Map<NeighborDirection, String>
    ): Set<String> = buildSet {
        add(targetHash)
        addAll(neighbors.values)
    }

    private fun indexWarehousesByPrefix(
        warehouses: List<Warehouse>,
        precision: Int
    ): Map<String, List<Warehouse>> =
        warehouses.groupBy { warehouse ->
            encodeGeohash(warehouse.latitude, warehouse.longitude, precision)
        }

    private fun queryWarehousesByPrefixes(
        prefixIndex: Map<String, List<Warehouse>>,
        searchPrefixes: Set<String>
    ): List<Warehouse> =
        prefixIndex
            .filterKeys { indexedPrefix ->
                searchPrefixes.any { queryPrefix ->
                    indexedPrefix.startsWith(queryPrefix) || queryPrefix.startsWith(indexedPrefix)
                }
            }
            .values
            .flatten()
            .distinct()

    private fun validateInputs(latitude: Double, longitude: Double, precision: Int) {
        require(latitude in MIN_LAT..MAX_LAT) { "Latitude must be within [$MIN_LAT, $MAX_LAT]" }
        require(longitude in MIN_LON..MAX_LON) { "Longitude must be within [$MIN_LON, $MAX_LON]" }
        require(precision in 1..12) { "Precision must be between 1 and 12" }
    }

    private fun clampLatitude(lat: Double): Double =
        max(MIN_LAT + 1e-6, min(MAX_LAT - 1e-6, lat))

    private fun wrapLongitude(lon: Double): Double = when {
        lon > MAX_LON -> lon - 360.0
        lon < MIN_LON -> lon + 360.0
        else -> lon
    }

    private data class BoundingBox(
        val minLat: Double,
        val maxLat: Double,
        val minLon: Double,
        val maxLon: Double
    )
}