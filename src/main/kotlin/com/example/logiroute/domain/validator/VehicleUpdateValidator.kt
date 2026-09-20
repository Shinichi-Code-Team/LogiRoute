package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.request.UpdateVehicleInput

class VehicleUpdateValidator(
    private val atLeastOneFieldValidator: AtLeastOneFieldValidator =
        AtLeastOneFieldValidator()
) : Validator<VehicleUpdateValidator.Input> {

    data class Input(
        val id: String?,
        val update: UpdateVehicleInput
    )

    override fun validate(
        value: Input
    ): ValidationResult {

        val errors = listOfNotNull(
            ValidationRules.validateVehicleId(
                value = value.id
            ),

            value.update.maxCapacityKg?.let {
                ValidationRules.validatePositiveDouble(
                    value = it,
                    field = ValidationField.MAX_CAPACITY_KG
                )
            },

            value.update.costPerKm?.let {
                ValidationRules.validatePositiveDouble(
                    value = it,
                    field = ValidationField.COST_PER_KM
                )
            },

            value.update.currentHubId?.let {
                ValidationRules.validateWarehouseId(
                    value = it,
                    field = ValidationField.CURRENT_HUB_ID
                )
            },

            atLeastOneFieldValidator.validate(
                values = mapOf(
                    "maxCapacityKg" to value.update.maxCapacityKg,
                    "costPerKm" to value.update.costPerKm,
                    "currentHubId" to value.update.currentHubId
                )
            )
        )

        return errors.toValidationResult()
    }
}
