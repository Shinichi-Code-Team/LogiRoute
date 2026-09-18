package com.example.logiroute.domain.validator

class LongitudeValidator :
    Validator<Double?, LongitudeValidationError> {

    override fun validate(
        value: Double?
    ): ValidationResult<LongitudeValidationError> {

        if (value == null) {
            return ValidationResult.Invalid(
                errors = listOf(
                    LongitudeValidationError.Null
                )
            )
        }

        if (value !in -180.0..180.0) {
            return ValidationResult.Invalid(
                errors = listOf(
                    LongitudeValidationError.OutOfRange(value)
                )
            )
        }

        return ValidationResult.Valid
    }
}