package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.request.UpdatePackageInput
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules

class PackageUpdateValidator(
    private val atLeastOneFieldValidator: AtLeastOneFieldValidator,
    private val validationRules: ValidationRules,
    private val validationResultMapper: ValidationResultMapper
) : Validator<PackageUpdateValidator.inputValidator> {

    data class inputValidator(
        val id: String?,
        val update: UpdatePackageInput
    )

    override fun validate(
        value: inputValidator
    ): ValidationResult {

        val errors = listOfNotNull(
            validationRules.validatePackageId(
                value = value.id
            ),

            value.update.weight?.let {
                validationRules.validatePositiveDouble(
                    value = it,
                    field = ValidationField.WEIGHT
                )
            },

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

            atLeastOneFieldValidator.validate(
                values = mapOf(
                    "weight" to value.update.weight,
                    "originHubId" to value.update.originHubId,
                    "destinationHubId" to value.update.destinationHubId,
                    "priority" to value.update.priority
                )
            )
        )

        return validationResultMapper.map(errors)
    }
}
