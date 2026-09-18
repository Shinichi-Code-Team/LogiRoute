package com.example.logiroute.domain.validator

sealed interface LatitudeValidationError : ValidationError {
    data object Null : LatitudeValidationError

    data class OutOfRange(
        val value: Double
    ) : LatitudeValidationError
}