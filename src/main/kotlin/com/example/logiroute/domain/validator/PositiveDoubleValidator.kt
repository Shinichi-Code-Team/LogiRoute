package com.example.logiroute.domain.validator

sealed interface PositiveDoubleValidationError : ValidationError {
    data object Null : PositiveDoubleValidationError

    data class NotPositive(
        val value: Double
    ) : PositiveDoubleValidationError
}

class PositiveDoubleValidator : Validator<Double?, PositiveDoubleValidationError> {

    override fun validate(
        value: Double?
    ): ValidationResult<PositiveDoubleValidationError> {

        if (value == null) {
            return ValidationResult.Invalid(
                errors = listOf(
                    PositiveDoubleValidationError.Null
                )
            )
        }

        if (value <= 0.0) {
            return ValidationResult.Invalid(
                errors = listOf(
                    PositiveDoubleValidationError.NotPositive(value)
                )
            )
        }

        return ValidationResult.Valid
    }
}