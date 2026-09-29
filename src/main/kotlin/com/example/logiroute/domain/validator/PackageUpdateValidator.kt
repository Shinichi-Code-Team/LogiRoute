package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.request.UpdatePackageInput
import com.example.logiroute.domain.validation.ValidationField
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationRules
import com.example.logiroute.domain.validation.toValidationResult

class PackageUpdateValidator(
    private val atLeastOneFieldValidator: AtLeastOneFieldValidator =
        AtLeastOneFieldValidator()
) : Validator<PackageUpdateValidator.inputValidator> {

    data class inputValidator(
        val id: String?,
        val update: UpdatePackageInput
    )

    override fun validate(
        value: inputValidator
    ): ValidationResult {

        val errors = listOfNotNull(
            ValidationRules.validatePackageId(
                value = value.id
            ),

            value.update.weight?.let {
                ValidationRules.validatePositiveDouble(
                    value = it,
                    field = ValidationField.WEIGHT
                )
            },

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

            atLeastOneFieldValidator.validate(
                values = mapOf(
                    "weight" to value.update.weight,
                    "originHubId" to value.update.originHubId,
                    "destinationHubId" to value.update.destinationHubId,
                    "priority" to value.update.priority
                )
            )
        )

        return errors.toValidationResult()
    }
}
