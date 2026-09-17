package com.example.logiroute.domain.validator

sealed interface NonNegativeIntValidationError : ValidationError {
    data object Null : NonNegativeIntValidationError

    data class Negative(
        val value: Int
    ) : NonNegativeIntValidationError
}

class NonNegativeIntValidator : Validator<Int?, NonNegativeIntValidationError> {

    override fun validate(
        value: Int?
    ): ValidationResult<NonNegativeIntValidationError> {

        if (value == null) {
            return ValidationResult.Invalid(
                errors = listOf(
                    NonNegativeIntValidationError.Null
                )
            )
        }

        if (value < 0) {
            return ValidationResult.Invalid(
                errors = listOf(
                    NonNegativeIntValidationError.Negative(value)
                )
            )
        }

        return ValidationResult.Valid
    }
}