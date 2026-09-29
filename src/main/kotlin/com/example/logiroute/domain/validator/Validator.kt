package com.example.logiroute.domain.validator

import com.example.logiroute.domain.validation.ValidationResult

interface Validator<T> {
    fun validate(value: T): ValidationResult
}