package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Warehouse
import kotlin.math.max
import kotlin.math.min

data class SpatialLookupResult(
    val targetGeohash: String,
    val neighborGeohashes: Map<SpatialDirection, String>,
    val nearbyWarehouses: List<Warehouse>
)

enum class SpatialDirection(val latitudeOffset: Int, val longitudeOffset: Int) {
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
        private const val MIN_LATITUDE = -90.0
        private const val MAX_LATITUDE = 90.0
        private const val MIN_LONGITUDE = -180.0
        private const val MAX_LONGITUDE = 180.0
        private const val FULL_LONGITUDE_SPAN = 360.0
        private const val POLAR_CLAMP_EPSILON = 1e-6
        private const val MIDPOINT_DIVISOR = 2.0
        private const val BITS_PER_BASE32_CHAR = 5
        private const val MIN_PRECISION = 1
        private const val MAX_PRECISION = 12
        private const val DEFAULT_PRECISION = 5
        private const val HIGH_BIT_SET_FLAG = 1
        private const val ZERO_BIT_VALUE = 0
        private const val INITIAL_BIT_ACCUMULATOR = 0
        private const val INITIAL_BIT_COUNT = 0
        private const val INITIAL_TURN_IS_LONGITUDE = true
        private const val BASE32_ALPHABET = "0123456789bcdefghjkmnpqrstuvwxyz"
        private val BASE32_BIT_MASKS = intArrayOf(16, 8, 4, 2, 1)
    }

    operator fun invoke(
        latitude: Double,
        longitude: Double,
        warehouses: List<Warehouse>,
        precision: Int = DEFAULT_PRECISION,
        includeNeighbors: Boolean = true
    ): SpatialLookupResult {
        ensureValidCoordinatesAndPrecision(latitude, longitude, precision)

        val targetGeohash = encodeCoordinatesToGeohash(latitude, longitude, precision)
        val neighbors = findSurroundingGeohashes(targetGeohash, includeNeighbors)
        val spatialIndex = groupWarehousesByGeohashPrefix(warehouses, precision)
        val searchPrefixes = collectSearchPrefixes(targetGeohash, neighbors)
        val matchedWarehouses = findWarehousesMatchingPrefixes(spatialIndex, searchPrefixes)

        return SpatialLookupResult(
            targetGeohash = targetGeohash,
            neighborGeohashes = neighbors,
            nearbyWarehouses = matchedWarehouses
        )
    }

    private fun ensureValidCoordinatesAndPrecision(
        latitude: Double,
        longitude: Double,
        precision: Int
    ) {
        require(latitude in MIN_LATITUDE..MAX_LATITUDE) {
            "Latitude must be between $MIN_LATITUDE and $MAX_LATITUDE"
        }
        require(longitude in MIN_LONGITUDE..MAX_LONGITUDE) {
            "Longitude must be between $MIN_LONGITUDE and $MAX_LONGITUDE"
        }
        require(precision in MIN_PRECISION..MAX_PRECISION) {
            "Precision must be between $MIN_PRECISION and $MAX_PRECISION"
        }
    }

    private fun encodeCoordinatesToGeohash(
        latitude: Double,
        longitude: Double,
        precision: Int
    ): String {
        var latitudeBounds = MIN_LATITUDE to MAX_LATITUDE
        var longitudeBounds = MIN_LONGITUDE to MAX_LONGITUDE
        var isLongitudeBit = INITIAL_TURN_IS_LONGITUDE
        var bitCount = INITIAL_BIT_COUNT
        var bitAccumulator = INITIAL_BIT_ACCUMULATOR
        val encodedBuilder = StringBuilder(precision)

        while (encodedBuilder.length < precision) {
            val nextBit = if (isLongitudeBit) {
                val (bit, updatedLongitudeBounds) = bisectRange(longitudeBounds, longitude)
                longitudeBounds = updatedLongitudeBounds
                bit
            } else {
                val (bit, updatedLatitudeBounds) = bisectRange(latitudeBounds, latitude)
                latitudeBounds = updatedLatitudeBounds
                bit
            }

            bitAccumulator = appendBit(bitAccumulator, nextBit)
            bitCount++
            isLongitudeBit = !isLongitudeBit

            if (bitCount == BITS_PER_BASE32_CHAR) {
                encodedBuilder.append(mapFiveBitsToBase32Char(bitAccumulator))
                bitCount = INITIAL_BIT_COUNT
                bitAccumulator = INITIAL_BIT_ACCUMULATOR
            }
        }

        return encodedBuilder.toString()
    }

    private fun bisectRange(
        bounds: Pair<Double, Double>,
        targetValue: Double
    ): Pair<Int, Pair<Double, Double>> {
        val midpoint = (bounds.first + bounds.second) / MIDPOINT_DIVISOR
        return if (targetValue >= midpoint) {
            HIGH_BIT_SET_FLAG to (midpoint to bounds.second)
        } else {
            ZERO_BIT_VALUE to (bounds.first to midpoint)
        }
    }

    private fun appendBit(accumulator: Int, bit: Int): Int =
        (accumulator shl HIGH_BIT_SET_FLAG) or bit

    private fun mapFiveBitsToBase32Char(fiveBitValue: Int): Char =
        BASE32_ALPHABET[fiveBitValue]

    private fun findSurroundingGeohashes(
        geohash: String,
        includeNeighbors: Boolean
    ): Map<SpatialDirection, String> {
        if (!includeNeighbors) return emptyMap()

        val cellBounds = decodeGeohashToBounds(geohash)
        val centerLatitude = (cellBounds.minLatitude + cellBounds.maxLatitude) / MIDPOINT_DIVISOR
        val centerLongitude = (cellBounds.minLongitude + cellBounds.maxLongitude) / MIDPOINT_DIVISOR
        val latitudeStep = cellBounds.maxLatitude - cellBounds.minLatitude
        val longitudeStep = cellBounds.maxLongitude - cellBounds.minLongitude

        return SpatialDirection.entries.associateWith { direction ->
            val shiftedLatitude = clampLatitude(
                centerLatitude + direction.latitudeOffset * latitudeStep
            )
            val shiftedLongitude = wrapLongitude(
                centerLongitude + direction.longitudeOffset * longitudeStep
            )
            encodeCoordinatesToGeohash(shiftedLatitude, shiftedLongitude, geohash.length)
        }
    }

    private fun decodeGeohashToBounds(geohash: String): GeohashBounds {
        var latitudeBounds = MIN_LATITUDE to MAX_LATITUDE
        var longitudeBounds = MIN_LONGITUDE to MAX_LONGITUDE
        var isLongitudeBit = INITIAL_TURN_IS_LONGITUDE

        for (character in geohash.lowercase()) {
            val characterCode = BASE32_ALPHABET.indexOf(character)
            require(characterCode >= ZERO_BIT_VALUE) {
                "Invalid Base-32 Geohash character: $character"
            }

            for (mask in BASE32_BIT_MASKS) {
                val bit = if ((characterCode and mask) != ZERO_BIT_VALUE) {
                    HIGH_BIT_SET_FLAG
                } else {
                    ZERO_BIT_VALUE
                }

                if (isLongitudeBit) {
                    longitudeBounds = applyDecodedBitToBounds(longitudeBounds, bit)
                } else {
                    latitudeBounds = applyDecodedBitToBounds(latitudeBounds, bit)
                }
                isLongitudeBit = !isLongitudeBit
            }
        }

        return GeohashBounds(
            minLatitude = latitudeBounds.first,
            maxLatitude = latitudeBounds.second,
            minLongitude = longitudeBounds.first,
            maxLongitude = longitudeBounds.second
        )
    }

    private fun applyDecodedBitToBounds(
        bounds: Pair<Double, Double>,
        bit: Int
    ): Pair<Double, Double> {
        val midpoint = (bounds.first + bounds.second) / MIDPOINT_DIVISOR
        return if (bit == HIGH_BIT_SET_FLAG) {
            midpoint to bounds.second
        } else {
            bounds.first to midpoint
        }
    }

    private fun clampLatitude(latitude: Double): Double =
        max(
            MIN_LATITUDE + POLAR_CLAMP_EPSILON,
            min(MAX_LATITUDE - POLAR_CLAMP_EPSILON, latitude)
        )

    private fun wrapLongitude(longitude: Double): Double = when {
        longitude > MAX_LONGITUDE -> longitude - FULL_LONGITUDE_SPAN
        longitude < MIN_LONGITUDE -> longitude + FULL_LONGITUDE_SPAN
        else -> longitude
    }

    private fun groupWarehousesByGeohashPrefix(
        warehouses: List<Warehouse>,
        precision: Int
    ): Map<String, List<Warehouse>> =
        warehouses.groupBy { warehouse ->
            encodeCoordinatesToGeohash(warehouse.latitude, warehouse.longitude, precision)
        }

    private fun collectSearchPrefixes(
        targetGeohash: String,
        neighbors: Map<SpatialDirection, String>
    ): Set<String> = buildSet {
        add(targetGeohash)
        addAll(neighbors.values)
    }

    private fun findWarehousesMatchingPrefixes(
        spatialIndex: Map<String, List<Warehouse>>,
        searchPrefixes: Set<String>
    ): List<Warehouse> =
        spatialIndex
            .filterKeys { indexedPrefix ->
                searchPrefixes.any { prefix ->
                    indexedPrefix.startsWith(prefix) || prefix.startsWith(indexedPrefix)
                }
            }
            .values
            .flatten()
            .distinct()

    private data class GeohashBounds(
        val minLatitude: Double,
        val maxLatitude: Double,
        val minLongitude: Double,
        val maxLongitude: Double
    )
}