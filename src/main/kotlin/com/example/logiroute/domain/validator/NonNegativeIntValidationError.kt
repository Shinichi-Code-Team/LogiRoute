package com.example.logiroute.domain.validator

sealed interface NonNegativeIntValidationError : ValidationError {
    data object Null : NonNegativeIntValidationError

    data class Negative(
        val value: Int
    ) : NonNegativeIntValidationError
}