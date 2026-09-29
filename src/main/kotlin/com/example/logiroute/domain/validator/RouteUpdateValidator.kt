package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.request.UpdateRouteInput
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationRules
import com.example.logiroute.domain.validation.toValidationResult

class RouteUpdateValidator(
    private val atLeastOneFieldValidator: AtLeastOneFieldValidator =
        AtLeastOneFieldValidator()
) : Validator<RouteUpdateValidator.inputValidator> {

    data class inputValidator(
        val id: String?,
        val update: UpdateRouteInput
    )

    override fun validate(
        value: inputValidator
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
