package com.example.logiroute.domain.validation

sealed interface ValidationResult {

    data object Valid : ValidationResult

    data class Invalid(
        val errors: List<ValidationError>
    ) : ValidationResult
}