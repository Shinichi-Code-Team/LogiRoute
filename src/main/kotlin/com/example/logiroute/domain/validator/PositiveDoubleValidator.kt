package com.example.logiroute.domain.validator


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