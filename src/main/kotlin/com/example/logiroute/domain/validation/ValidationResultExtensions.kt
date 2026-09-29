package com.example.logiroute.domain.validation

import com.example.logiroute.domain.validation.ValidationError
import com.example.logiroute.domain.validation.ValidationResult

fun List<ValidationError>.toValidationResult(): ValidationResult {
    return if (isEmpty()) {
        ValidationResult.Valid
    } else {
        ValidationResult.Invalid(this)
    }
}