package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.request.UpdateWarehouseInput

class WarehouseUpdateValidator(
    private val idValidator: IdValidator,
    private val nonBlankValidator: NonBlankValidator,
    private val latitudeValidator: LatitudeValidator,
    private val longitudeValidator: LongitudeValidator
) {

    fun validate(
        id: String?,
        input: UpdateWarehouseInput
    ): ValidationResult<WarehouseUpdateValidationError> {

        val idErrors =
            when (val result = idValidator.validate(id)) {
                ValidationResult.Valid -> emptyList()
                is ValidationResult.Invalid ->
                    result.errors.map(WarehouseUpdateValidationError::InvalidId)
            }

        val fieldErrors = listOfNotNull(
            input.name?.let {
                when (val result = nonBlankValidator.validate(it)) {
                    ValidationResult.Valid -> null
                    is ValidationResult.Invalid ->
                        result.errors.map(WarehouseUpdateValidationError::InvalidName)
                }
            },

            input.regionalZone?.let {
                when (val result = nonBlankValidator.validate(it)) {
                    ValidationResult.Valid -> null
                    is ValidationResult.Invalid ->
                        result.errors.map(WarehouseUpdateValidationError::InvalidRegionalZone)
                }
            },

            input.latitude?.let {
                when (val result = latitudeValidator.validate(it)) {
                    ValidationResult.Valid -> null
                    is ValidationResult.Invalid ->
                        result.errors.map(WarehouseUpdateValidationError::InvalidLatitude)
                }
            },

            input.longitude?.let {
                when (val result = longitudeValidator.validate(it)) {
                    ValidationResult.Valid -> null
                    is ValidationResult.Invalid ->
                        result.errors.map(WarehouseUpdateValidationError::InvalidLongitude)
                }
            }
        ).flatten()

        val noFieldsError =
            if (
                input.name == null &&
                input.regionalZone == null &&
                input.latitude == null &&
                input.longitude == null
            ) {
                listOf(WarehouseUpdateValidationError.NoFieldsProvided)
            } else {
                emptyList()
            }

        val errors = idErrors + fieldErrors + noFieldsError

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}