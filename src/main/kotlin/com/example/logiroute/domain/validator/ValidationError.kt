package com.example.logiroute.domain.validator

data class ValidationError(
    val field: ValidationField,
    val reason: ValidationReason
)