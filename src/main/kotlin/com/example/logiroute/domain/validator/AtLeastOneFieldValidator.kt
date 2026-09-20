package com.example.logiroute.domain.validator

class AtLeastOneFieldValidator {

    fun validate(
        values: Map<String, Any?>
    ): ValidationError? {
        return if (values.values.none { it != null }) {
            ValidationError(
                field = ValidationField.UPDATE_FIELDS,
                reason = ValidationReason.NO_FIELDS_PROVIDED
            )
        } else {
            null
        }
    }
}