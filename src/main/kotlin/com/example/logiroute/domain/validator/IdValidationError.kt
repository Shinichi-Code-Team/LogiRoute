package com.example.logiroute.domain.validator

sealed interface IdValidationError : ValidationError {
    data object NullOrBlank : IdValidationError

    data class InvalidFormat(
        val value: String
    ) : IdValidationError
}