package com.example.logiroute.domain.validator

sealed interface NonBlankValidationError : ValidationError {
    data object NullOrBlank : NonBlankValidationError
}

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