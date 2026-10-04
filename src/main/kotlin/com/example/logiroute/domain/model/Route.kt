package com.example.logiroute.domain.model

import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationRules

data class Route(
    val id: String,
    val origin: Warehouse,
    val destination: Warehouse,
    val distanceKm: Double,
    val typicalDelayMin: Int
) {

    init {
        val validationRules = ValidationRules()
        val errors = listOfNotNull(
            validationRules.validateRouteId(id),

            validationRules.validateWarehouseId(
                value = origin.id,
                field = ValidationField.ORIGIN_HUB_ID
            ),

            validationRules.validateWarehouseId(
                value = destination.id,
                field = ValidationField.DESTINATION_HUB_ID
            ),

            validationRules.validatePositiveDouble(
                value = distanceKm,
                field = ValidationField.DISTANCE_KM
            ),

            validationRules.validateNonNegativeInt(
                value = typicalDelayMin,
                field = ValidationField.TYPICAL_DELAY_MIN
            )
        )

        if (errors.isNotEmpty()) {
            throw LogisticsException.EntityValidationException(errors)
        }
    }
}