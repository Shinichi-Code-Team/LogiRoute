package com.example.logiroute.domain.validator

interface Validator<T> {
    fun validate(value: T): ValidationResult
}