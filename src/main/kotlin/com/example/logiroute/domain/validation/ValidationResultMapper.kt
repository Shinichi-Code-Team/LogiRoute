package com.example.logiroute.domain.validation

class ValidationResultMapper {

    fun map(errors: List<ValidationError>): ValidationResult {
        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors.toList())
        }
    }
}