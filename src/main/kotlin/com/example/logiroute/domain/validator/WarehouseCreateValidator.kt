package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.Warehouse

class WarehouseCreateValidator : Validator<Warehouse> {

    override fun validate(
        value: Warehouse
    ): ValidationResult {

        val errors = listOfNotNull(
            ValidationRules.validateWarehouseId(
                value = value.id
            ),
            ValidationRules.validateNonBlank(
                value = value.name,
                field = ValidationField.NAME
            ),
            ValidationRules.validateNonBlank(
                value = value.regionalZone,
                field = ValidationField.REGIONAL_ZONE
            ),
            ValidationRules.validateLatitude(
                value = value.latitude
            ),
            ValidationRules.validateLongitude(
                value = value.longitude
            )
        )

        return errors.toValidationResult()
    }
}
