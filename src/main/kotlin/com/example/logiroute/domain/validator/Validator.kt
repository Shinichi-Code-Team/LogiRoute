package com.example.logiroute.domain.validator
interface Validator<T, E : ValidationError> {
    fun validate(value: T): ValidationResult<E>
}