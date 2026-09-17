package com.example.logiroute.domain.validator

sealed interface IdValidationError : ValidationError {
    data object NullOrBlank : IdValidationError

    data class InvalidFormat(
        val value: String
    ) : IdValidationError
}
class IdValidator( private val pattern: Regex ) : Validator<String?, IdValidationError> {
    override fun validate(
        value: String?
    ): ValidationResult<IdValidationError> {

        if (value.isNullOrBlank()) {
            return ValidationResult.Invalid(
                errors = listOf(
                    IdValidationError.NullOrBlank
                )
            )
        }

        if (!pattern.matches(value)) {
            return ValidationResult.Invalid(
                errors = listOf(
                    IdValidationError.InvalidFormat(value)
                )
            )
        }

        return ValidationResult.Valid
    }
}