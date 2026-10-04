package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.request.UpdateRouteInput
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules

class RouteUpdateValidator(
    private val atLeastOneFieldValidator: AtLeastOneFieldValidator,
    private val validationRules: ValidationRules,
    private val validationResultMapper: ValidationResultMapper
) : Validator<RouteUpdateValidator.inputValidator> {

    data class inputValidator(
        val id: String?,
        val update: UpdateRouteInput
    )

    override fun validate(
        value: inputValidator
    ): ValidationResult {

        val errors = listOfNotNull(
            validationRules.validateRouteId(
                value = value.id
            ),

            value.update.originHubId?.let {
                validationRules.validateWarehouseId(
                    value = it,
                    field = ValidationField.ORIGIN_HUB_ID
                )
            },

            value.update.destinationHubId?.let {
                validationRules.validateWarehouseId(
                    value = it,
                    field = ValidationField.DESTINATION_HUB_ID
                )
            },

            value.update.distanceKm?.let {
                validationRules.validatePositiveDouble(
                    value = it,
                    field = ValidationField.DISTANCE_KM
                )
            },

            value.update.typicalDelayMin?.let {
                validationRules.validateNonNegativeInt(
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

        return validationResultMapper.map(errors)    }
}
