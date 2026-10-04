package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.request.UpdateWarehouseInput
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules

class WarehouseUpdateValidator(
    private val atLeastOneFieldValidator: AtLeastOneFieldValidator,
    private val validationRules: ValidationRules,
    private val validationResultMapper: ValidationResultMapper
) : Validator<WarehouseUpdateValidator.inputValidator> {

    data class inputValidator(
        val id: String?,
        val update: UpdateWarehouseInput
    )

    override fun validate(
        value: inputValidator
    ): ValidationResult {

        val errors = listOfNotNull(
            validationRules.validateWarehouseId(
                value = value.id
            ),

            value.update.name?.let {
                validationRules.validateNonBlank(
                    value = it,
                    field = ValidationField.NAME
                )
            },

            value.update.regionalZone?.let {
                validationRules.validateNonBlank(
                    value = it,
                    field = ValidationField.REGIONAL_ZONE
                )
            },

            value.update.latitude?.let {
                validationRules.validateLatitude(
                    value = it
                )
            },

            value.update.longitude?.let {
                validationRules.validateLongitude(
                    value = it
                )
            },

            atLeastOneFieldValidator.validate(
                values = mapOf(
                    "name" to value.update.name,
                    "regionalZone" to value.update.regionalZone,
                    "latitude" to value.update.latitude,
                    "longitude" to value.update.longitude
                )
            )
        )

        return validationResultMapper.map(errors)    }
}
