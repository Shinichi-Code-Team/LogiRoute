package com.example.logiroute.domain.validation


fun List<ValidationError>.toValidationResult(): ValidationResult {
    return if (isEmpty()) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid(this)
    }
}