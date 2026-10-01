package com.example.logiroute.domain.validation



data class ValidationError(
    val field: ValidationField,
    val reason: ValidationReason
)