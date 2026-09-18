package com.example.logiroute.domain.validator

sealed interface PositiveDoubleValidationError : ValidationError {
    data object Null : PositiveDoubleValidationError

    data class NotPositive(
        val value: Double
    ) : PositiveDoubleValidationError
}