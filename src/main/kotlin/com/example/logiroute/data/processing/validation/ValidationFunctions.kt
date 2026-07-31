package com.example.logiroute.data.processing.validation

const val INVALID_DOUBLE_VALUE = -1.0
const val INVALID_INT_VALUE = -1

fun hasExpectedColumnCount(columns: List<String>, expectedCount: Int): Boolean {
    return columns.size == expectedCount
}

fun isNotBlank(value: String): Boolean {
    return value.isNotBlank()
}

fun parsePositiveDoubleOrInvalid(value: String): Double {
    val parsedNumber = value.trim().toDoubleOrNull()

    return if (parsedNumber != null && parsedNumber > 0.0) {
        parsedNumber
    } else {
        INVALID_DOUBLE_VALUE
    }
}

fun parseNonNegativeIntOrInvalid(value: String): Int {
    val parsedNumber = value.trim().toIntOrNull()

    return if (parsedNumber != null && parsedNumber >= 0) {
        parsedNumber
    } else {
        INVALID_INT_VALUE
    }
}
fun parseCoordinateOrInvalid(value: String, minValue: Double, maxValue: Double): Double {
    val parsedNumber = value.trim().toDoubleOrNull()

    return if (parsedNumber != null && parsedNumber in minValue..maxValue) {
        parsedNumber
    } else {
        INVALID_DOUBLE_VALUE
    }
}
