package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.request.UpdateWarehouseInput

class WarehouseUpdateValidator(
    private val atLeastOneFieldValidator: AtLeastOneFieldValidator =
        AtLeastOneFieldValidator()
) : Validator<WarehouseUpdateValidator.Input> {

    data class Input(
        val id: String?,
        val update: UpdateWarehouseInput
    )

    override fun validate(
        value: Input
    ): ValidationResult {

        val errors = listOfNotNull(
            ValidationRules.validateWarehouseId(
                value = value.id
            ),

            value.update.name?.let {
                ValidationRules.validateNonBlank(
                    value = it,
                    field = ValidationField.NAME
                )
            },

            value.update.regionalZone?.let {
                ValidationRules.validateNonBlank(
                    value = it,
                    field = ValidationField.REGIONAL_ZONE
                )
            },

            value.update.latitude?.let {
                ValidationRules.validateLatitude(
                    value = it
                )
            },

            value.update.longitude?.let {
                ValidationRules.validateLongitude(
                    value = it
                )
            },

            atLeastOneFieldValidator.validate(
                values = mapOf(
                    "name" to value.update.name,
                    "regionalZone" to value.update.regionalZone,
                    "latitude" to value.update.latitude,
                    "longitude" to value.update.longitude
                )
            )
        )

        return errors.toValidationResult()
    }
}
