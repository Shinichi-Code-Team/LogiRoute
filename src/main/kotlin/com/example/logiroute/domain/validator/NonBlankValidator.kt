package com.example.logiroute.domain.validator

class NonBlankValidator : Validator<String?, NonBlankValidationError> {

    override fun validate(
        value: String?
    ): ValidationResult<NonBlankValidationError> {

        if (value.isNullOrBlank()) {
            return ValidationResult.Invalid(
                errors = listOf(
                    NonBlankValidationError.NullOrBlank
                )
            )
        }

        return ValidationResult.Valid
    }
}