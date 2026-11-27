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
        validateRange(latitude, MIN_LATITUDE, MAX_LATITUDE, "Latitude")
        validateRange(longitude, MIN_LONGITUDE, MAX_LONGITUDE, "Longitude")
        validatePrecision(precision)
    }

    private fun validateRange(value: Double, minValue: Double, maxValue: Double, fieldName: String) {
        require(value in minValue..maxValue) { "$fieldName must be between $minValue and $maxValue" }
    }

    private fun validatePrecision(precision: Int) {
        require(precision in MIN_PRECISION..MAX_PRECISION) {
            "Precision must be between $MIN_PRECISION and $MAX_PRECISION"
        }
    }

    private fun encodeCoordinatesToGeohash(
        latitude: Double,
        longitude: Double,
        precision: Int
    ): String {
        var state = EncodingState(
            latitudeBounds = MIN_LATITUDE to MAX_LATITUDE,
            longitudeBounds = MIN_LONGITUDE to MAX_LONGITUDE
        )
        val encodedBuilder = StringBuilder(precision)

        while (encodedBuilder.length < precision) {
            val (nextBit, updatedState) = stepEncodingBit(state, latitude, longitude)
            state = appendEncodedBit(updatedState, nextBit, encodedBuilder)
        }

        return encodedBuilder.toString()
    }

    private fun stepEncodingBit(
        state: EncodingState,
        latitude: Double,
        longitude: Double
    ): Pair<Int, EncodingState> {
        return if (state.isLongitudeBit) {
            val (bit, nextLonBounds) = bisectRange(state.longitudeBounds, longitude)
            bit to state.copy(longitudeBounds = nextLonBounds)
        } else {
            val (bit, nextLatBounds) = bisectRange(state.latitudeBounds, latitude)
            bit to state.copy(latitudeBounds = nextLatBounds)
        }
    }

    private fun appendEncodedBit(
        state: EncodingState,
        bit: Int,
        encodedBuilder: StringBuilder
    ): EncodingState {
        val nextAccumulator = appendBit(state.bitAccumulator, bit)
        val nextBitCount = state.bitCount + HIGH_BIT_SET_FLAG
        val nextTurn = !state.isLongitudeBit

        return if (nextBitCount == BITS_PER_BASE32_CHAR) {
            encodedBuilder.append(mapFiveBitsToBase32Char(nextAccumulator))
            state.copy(
                isLongitudeBit = nextTurn,
                bitCount = INITIAL_BIT_COUNT,
                bitAccumulator = INITIAL_BIT_ACCUMULATOR
            )
        } else {
            state.copy(
                isLongitudeBit = nextTurn,
                bitCount = nextBitCount,
                bitAccumulator = nextAccumulator
            )
        }
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
        return SpatialDirection.entries.associateWith { direction ->
            neighborGeohashFor(cellBounds, direction, geohash.length)
        }
    }

    private fun neighborGeohashFor(
        cellBounds: GeohashBounds,
        direction: SpatialDirection,
        precision: Int
    ): String {
        val shiftedLatitude = shiftedLatitudeFor(cellBounds, direction)
        val shiftedLongitude = shiftedLongitudeFor(cellBounds, direction)
        return encodeCoordinatesToGeohash(shiftedLatitude, shiftedLongitude, precision)
    }

    private fun shiftedLatitudeFor(cellBounds: GeohashBounds, direction: SpatialDirection): Double {
        val centerLatitude = (cellBounds.minLatitude + cellBounds.maxLatitude) / MIDPOINT_DIVISOR
        val latitudeStep = cellBounds.maxLatitude - cellBounds.minLatitude
        return clampLatitude(centerLatitude + direction.latitudeOffset * latitudeStep)
    }

    private fun shiftedLongitudeFor(cellBounds: GeohashBounds, direction: SpatialDirection): Double {
        val centerLongitude = (cellBounds.minLongitude + cellBounds.maxLongitude) / MIDPOINT_DIVISOR
        val longitudeStep = cellBounds.maxLongitude - cellBounds.minLongitude
        return wrapLongitude(centerLongitude + direction.longitudeOffset * longitudeStep)
    }

    private fun decodeGeohashToBounds(geohash: String): GeohashBounds {
        var bounds = GeohashBounds(
            minLatitude = MIN_LATITUDE,
            maxLatitude = MAX_LATITUDE,
            minLongitude = MIN_LONGITUDE,
            maxLongitude = MAX_LONGITUDE
        )
        var isLongitudeBit = INITIAL_TURN_IS_LONGITUDE

        for (character in geohash.lowercase()) {
            val (updatedBounds, nextTurn) = decodeCharacter(character, bounds, isLongitudeBit)
            bounds = updatedBounds
            isLongitudeBit = nextTurn
        }

        return bounds
    }

    private fun decodeCharacter(
        character: Char,
        initialBounds: GeohashBounds,
        initialTurnIsLongitude: Boolean
    ): Pair<GeohashBounds, Boolean> {
        val characterCode = base32IndexOf(character)
        var bounds = initialBounds
        var isLongitudeBit = initialTurnIsLongitude

        for (mask in BASE32_BIT_MASKS) {
            val bit = extractBit(characterCode, mask)
            bounds = applyDecodedBit(bounds, bit, isLongitudeBit)
            isLongitudeBit = !isLongitudeBit
        }

        return bounds to isLongitudeBit
    }

    private fun base32IndexOf(character: Char): Int {
        val index = BASE32_ALPHABET.indexOf(character)
        require(index >= ZERO_BIT_VALUE) { "Invalid Base-32 Geohash character: $character" }
        return index
    }

    private fun extractBit(characterCode: Int, mask: Int): Int =
        if ((characterCode and mask) != ZERO_BIT_VALUE) HIGH_BIT_SET_FLAG else ZERO_BIT_VALUE

    private fun applyDecodedBit(
        bounds: GeohashBounds,
        bit: Int,
        isLongitudeBit: Boolean
    ): GeohashBounds {
        return if (isLongitudeBit) {
            val nextLon = applyDecodedBitToBounds(bounds.minLongitude to bounds.maxLongitude, bit)
            bounds.copy(minLongitude = nextLon.first, maxLongitude = nextLon.second)
        } else {
            val nextLat = applyDecodedBitToBounds(bounds.minLatitude to bounds.maxLatitude, bit)
            bounds.copy(minLatitude = nextLat.first, maxLatitude = nextLat.second)
        }
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
            .filterKeys { indexedPrefix -> matchesAnyPrefix(indexedPrefix, searchPrefixes) }
            .values
            .flatten()
            .distinct()

    private fun matchesAnyPrefix(indexedPrefix: String, searchPrefixes: Set<String>): Boolean =
        searchPrefixes.any { prefix ->
            indexedPrefix.startsWith(prefix) || prefix.startsWith(indexedPrefix)
        }

    private data class EncodingState(
        val latitudeBounds: Pair<Double, Double>,
        val longitudeBounds: Pair<Double, Double>,
        val isLongitudeBit: Boolean = INITIAL_TURN_IS_LONGITUDE,
        val bitCount: Int = INITIAL_BIT_COUNT,
        val bitAccumulator: Int = INITIAL_BIT_ACCUMULATOR
    )

    private data class GeohashBounds(
        val minLatitude: Double,
        val maxLatitude: Double,
        val minLongitude: Double,
        val maxLongitude: Double
    )
}