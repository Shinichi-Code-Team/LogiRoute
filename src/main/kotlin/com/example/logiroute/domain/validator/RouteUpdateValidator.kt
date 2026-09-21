package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.request.UpdateRouteInput

class RouteUpdateValidator(
    private val atLeastOneFieldValidator: AtLeastOneFieldValidator =
        AtLeastOneFieldValidator()
) : Validator<RouteUpdateValidator.Input> {

    data class Input(
        val id: String?,
        val update: UpdateRouteInput
    )

    override fun validate(
        value: Input
    ): ValidationResult {

        val errors = listOfNotNull(
            ValidationRules.validateRouteId(
                value = value.id
            ),

            value.update.originHubId?.let {
                ValidationRules.validateWarehouseId(
                    value = it,
                    field = ValidationField.ORIGIN_HUB_ID
                )
            },

            value.update.destinationHubId?.let {
                ValidationRules.validateWarehouseId(
                    value = it,
                    field = ValidationField.DESTINATION_HUB_ID
                )
            },

            value.update.distanceKm?.let {
                ValidationRules.validatePositiveDouble(
                    value = it,
                    field = ValidationField.DISTANCE_KM
                )
            },

            value.update.typicalDelayMin?.let {
                ValidationRules.validateNonNegativeInt(
                    value = it,
                    field = ValidationField.TYPICAL_DELAY_MIN
                )
            },

            atLeastOneFieldValidator.validate(
                values = mapOf(
                    "originHubId" to value.update.originHubId,
                    "destinationHubId" to value.update.destinationHubId,
                    "distanceKm" to value.update.distanceKm,
                    "typicalDelayMin" to value.update.typicalDelayMin
                )
            )
        )

        return errors.toValidationResult()
    }
}
