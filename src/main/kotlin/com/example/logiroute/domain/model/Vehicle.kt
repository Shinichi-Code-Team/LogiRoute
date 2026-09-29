package com.example.logiroute.domain.model

import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.ValidationField
import com.example.logiroute.domain.validator.ValidationRules

data class Vehicle(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse,
    val loadedPackages: MutableList<Package> = mutableListOf()
) {

    init {
        val errors = listOfNotNull(

            ValidationRules.validateVehicleId(id),

            ValidationRules.validateWarehouseId(
                value = currentHub.id,
                field = ValidationField.CURRENT_HUB_ID
            ),

            ValidationRules.validatePositiveDouble(
                value = maxCapacityKg,
                field = ValidationField.MAX_CAPACITY_KG
            ),

            ValidationRules.validatePositiveDouble(
                value = costPerKm,
                field = ValidationField.COST_PER_KM
            )
        )

        if (errors.isNotEmpty()) {
            throw LogisticsException.EntityValidationException(errors)
        }
    }
}