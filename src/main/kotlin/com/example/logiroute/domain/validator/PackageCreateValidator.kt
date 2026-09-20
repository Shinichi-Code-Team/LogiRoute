package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.Package

class PackageCreateValidator : Validator<Package> {

    override fun validate(
        value: Package
    ): ValidationResult {

        val errors = listOfNotNull(
            ValidationRules.validatePackageId(
                value = value.id
            ),
            ValidationRules.validatePositiveDouble(
                value = value.weight,
                field = ValidationField.WEIGHT
            ),
            ValidationRules.validateWarehouseId(
                value = value.origin.id,
                field = ValidationField.ORIGIN_HUB_ID
            ),
            ValidationRules.validateWarehouseId(
                value = value.destination.id,
                field = ValidationField.DESTINATION_HUB_ID
            )
        )

        return errors.toValidationResult()
    }
}