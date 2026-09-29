package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.request.UpdateVehicleInput
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationRules
import com.example.logiroute.domain.validation.toValidationResult

class VehicleUpdateValidator(
    private val atLeastOneFieldValidator: AtLeastOneFieldValidator =
        AtLeastOneFieldValidator()
) : Validator<VehicleUpdateValidator.inputValidator> {

    data class inputValidator(
        val id: String?,
        val update: UpdateVehicleInput
    )

    override fun validate(
        value: inputValidator
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
