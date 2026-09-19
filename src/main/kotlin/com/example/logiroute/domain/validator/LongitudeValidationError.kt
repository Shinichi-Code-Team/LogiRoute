package com.example.logiroute.domain.validator

sealed interface LongitudeValidationError : ValidationError {
    data object Null : LongitudeValidationError

    data class OutOfRange(
        val value: Double
    ) : LongitudeValidationError
}