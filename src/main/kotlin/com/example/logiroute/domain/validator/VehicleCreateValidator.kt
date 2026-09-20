package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.Vehicle

class VehicleCreateValidator : Validator<Vehicle> {

    override fun validate(
        value: Vehicle
    ): ValidationResult {

        val errors = listOfNotNull(
            ValidationRules.validateVehicleId(
                value = value.id
            ),
            ValidationRules.validateWarehouseId(
                value = value.currentHub.id,
                field = ValidationField.CURRENT_HUB_ID
            ),
            ValidationRules.validatePositiveDouble(
                value = value.maxCapacityKg,
                field = ValidationField.MAX_CAPACITY_KG
            ),
            ValidationRules.validatePositiveDouble(
                value = value.costPerKm,
                field = ValidationField.COST_PER_KM
            )
        )

        return errors.toValidationResult()
    }
}
