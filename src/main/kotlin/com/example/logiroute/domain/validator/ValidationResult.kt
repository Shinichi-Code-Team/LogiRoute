package com.example.logiroute.domain.validator
sealed interface ValidationResult<out E : ValidationError> {
    data object Valid : ValidationResult<Nothing>

    data class Invalid<E : ValidationError>(
        val errors: List<E>
    ) : ValidationResult<E>
}