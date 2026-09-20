package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.request.UpdatePackageInput

class PackageUpdateValidator(
    private val atLeastOneFieldValidator: AtLeastOneFieldValidator =
        AtLeastOneFieldValidator()
) : Validator<PackageUpdateValidator.Input> {

    data class Input(
        val id: String?,
        val update: UpdatePackageInput
    )

    override fun validate(
        value: Input
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