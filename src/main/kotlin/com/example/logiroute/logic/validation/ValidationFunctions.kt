package com.example.logiroute.logic.validation

const val DEFAULT_INVALID_DOUBLE = -1.0
const val DEFAULT_INVALID_INT = -1
const val ROUTE_COLUMN_COUNT = 5

fun isNotBlank(row: String): Boolean {

    return row.isNotBlank()
}

fun validateColumnCount(columns: List<String>, expectedCount: Int): Boolean {
    if (columns.size == expectedCount) {
        return true
    } else {
        return false
    }
}

fun isPositiveDouble(value: String): Double {
    val cleanValue = value.trim()
    val parsedNumber = cleanValue.toDoubleOrNull()

    if (parsedNumber != null && parsedNumber >= 0.0) {
        return parsedNumber
    } else {
        return DEFAULT_INVALID_DOUBLE
    }
}

fun isPositiveInt(value: String): Int {
    val cleanValue = value.trim()
    val parsedNumber = cleanValue.toIntOrNull()

    if (parsedNumber != null && parsedNumber >= 0) {
        return parsedNumber
    } else {
        return DEFAULT_INVALID_INT
    }
}

fun parsePriority(value: String): String {
    val cleanValue = value.trim().uppercase()

    if (cleanValue == "URGENT" || cleanValue == "STANDARD") {
        return cleanValue
    } else {
        return "LOW"
    }
}