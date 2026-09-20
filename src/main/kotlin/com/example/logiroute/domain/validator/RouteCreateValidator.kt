package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.Route

class RouteCreateValidator : Validator<Route> {

    override fun validate(
        value: Route
    ): ValidationResult {

        val errors = listOfNotNull(
            ValidationRules.validateRouteId(
                value = value.id
            ),
            ValidationRules.validateWarehouseId(
                value = value.origin.id,
                field = ValidationField.ORIGIN_HUB_ID
            ),
            ValidationRules.validateWarehouseId(
                value = value.destination.id,
                field = ValidationField.DESTINATION_HUB_ID
            ),
            ValidationRules.validatePositiveDouble(
                value = value.distanceKm,
                field = ValidationField.DISTANCE_KM
            ),
            ValidationRules.validateNonNegativeInt(
                value = value.typicalDelayMin,
                field = ValidationField.TYPICAL_DELAY_MIN
            )
        )

        return errors.toValidationResult()
    }
}