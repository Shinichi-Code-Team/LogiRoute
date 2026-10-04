package com.example.logiroute.domain.model

import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationRules

data class Vehicle(
    val id: String,
    val maxCapacityKg: Double,
    val costPerKm: Double,
    val currentHub: Warehouse,
    val loadedPackages: MutableList<Package> = mutableListOf()
) {

    init {
        val validationRules = ValidationRules()
        val errors = listOfNotNull(

            validationRules.validateVehicleId(id),

            validationRules.validateWarehouseId(
                value = currentHub.id,
                field = ValidationField.CURRENT_HUB_ID
            ),

            validationRules.validatePositiveDouble(
                value = maxCapacityKg,
                field = ValidationField.MAX_CAPACITY_KG
            ),

            validationRules.validatePositiveDouble(
                value = costPerKm,
                field = ValidationField.COST_PER_KM
            )
        )

        if (errors.isNotEmpty()) {
            throw LogisticsException.EntityValidationException(errors)
        }
    }
}