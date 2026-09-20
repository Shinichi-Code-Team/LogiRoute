package com.example.logiroute.domain.validator

sealed interface LongitudeValidationError : ValidationError {
    data object Null : LongitudeValidationError

    data class OutOfRange(
        val value: Double
    ) : LongitudeValidationError
}

class LongitudeValidator : Validator<Double?, LongitudeValidationError> {

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