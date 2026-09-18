package com.example.logiroute.domain.validator.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.NonBlankValidator
import com.example.logiroute.domain.validator.LatitudeValidator
import com.example.logiroute.domain.validator.LongitudeValidator
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.Validator

class WarehouseCreateValidator(
    private val idValidator: IdValidator,
    private val nonBlankValidator: NonBlankValidator,
    private val latitudeValidator: LatitudeValidator,
    private val longitudeValidator: LongitudeValidator
) : Validator<Warehouse, WarehouseValidationError> {

    override fun validate(
        value: Warehouse
    ): ValidationResult<WarehouseValidationError> {

        val errors = mutableListOf<WarehouseValidationError>()

        if (idValidator.validate(value.id) is ValidationResult.Invalid) {
            errors.add(WarehouseValidationError.InvalidId)
        }

        if (nonBlankValidator.validate(value.name) is ValidationResult.Invalid) {
            errors.add(WarehouseValidationError.InvalidName)
        }

        if (nonBlankValidator.validate(value.regionalZone) is ValidationResult.Invalid) {
            errors.add(WarehouseValidationError.InvalidRegionalZone)
        }

        if (latitudeValidator.validate(value.latitude) is ValidationResult.Invalid) {
            errors.add(WarehouseValidationError.InvalidLatitude)
        }

        if (longitudeValidator.validate(value.longitude) is ValidationResult.Invalid) {
            errors.add(WarehouseValidationError.InvalidLongitude)
        }

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}