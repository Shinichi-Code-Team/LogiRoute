package com.example.logiroute.domain.validation

import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationReason

data class ValidationError(
    val field: ValidationField,
    val reason: ValidationReason
)