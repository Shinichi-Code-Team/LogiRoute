package com.example.logiroute.domain.validator

sealed interface NonBlankValidationError : ValidationError {
    data object NullOrBlank : NonBlankValidationError
}