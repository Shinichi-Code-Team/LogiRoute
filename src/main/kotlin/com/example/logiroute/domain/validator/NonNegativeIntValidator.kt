package com.example.logiroute.domain.validator

class NonNegativeIntValidator :
    Validator<Int?, NonNegativeIntValidationError> {

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