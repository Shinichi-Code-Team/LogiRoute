package com.example.logiroute.logic.validation

const val DEFAULT_INVALID_DOUBLE = -1.0
const val DEFAULT_INVALID_INT = -1

fun isNotBlank(row: String): Boolean {
    return row.isNotBlank()
}


fun validateColumnCount(columns: List<String>, expectedCount: Int): Boolean {
    return columns.size == expectedCount
}

fun isPositiveDouble(value: String): Double {
    val parsedNumber = value.trim().toDoubleOrNull()

    return if (parsedNumber != null && parsedNumber > 0.0) {
        parsedNumber
    } else {
        DEFAULT_INVALID_DOUBLE
    }
}

fun isPositiveInt(value: String): Int {
    val parsedNumber = value.trim().toIntOrNull()

    return if (parsedNumber != null && parsedNumber >= 0) {
        parsedNumber
    } else {
        DEFAULT_INVALID_INT
    }
}