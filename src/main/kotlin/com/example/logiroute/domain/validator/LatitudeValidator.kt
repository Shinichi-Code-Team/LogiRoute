package com.example.logiroute.domain.validator

sealed interface LatitudeValidationError : ValidationError {
    data object Null : LatitudeValidationError

    data class OutOfRange(
        val value: Double
    ) : LatitudeValidationError
}

class LatitudeValidator : Validator<Double?, LatitudeValidationError> {

    override fun validate(
        value: Double?
    ): ValidationResult<LatitudeValidationError> {

        if (value == null) {
            return ValidationResult.Invalid(
                errors = listOf(
                    LatitudeValidationError.Null
                )
            )
        }

        if (value !in -90.0..90.0) {
            return ValidationResult.Invalid(
                errors = listOf(
                    LatitudeValidationError.OutOfRange(value)
                )
            )
        }

        return ValidationResult.Valid
    }
}