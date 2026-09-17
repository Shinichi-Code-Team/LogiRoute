package com.example.logiroute.domain.validator

sealed interface AtLeastOneFieldValidationError : ValidationError {
    data object NoFieldsProvided : AtLeastOneFieldValidationError
}

class AtLeastOneFieldValidator :
    Validator<Map<String, Any?>, AtLeastOneFieldValidationError> {

    override fun validate(
        value: Map<String, Any?>
    ): ValidationResult<AtLeastOneFieldValidationError> {

        if (value.values.none { it != null }) {
            return ValidationResult.Invalid(
                errors = listOf(
                    AtLeastOneFieldValidationError.NoFieldsProvided
                )
            )
        }

        return ValidationResult.Valid
    }
}