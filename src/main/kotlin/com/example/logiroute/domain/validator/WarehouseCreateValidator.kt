package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.Warehouse

class WarehouseCreateValidator(
    private val idValidator: IdValidator,
    private val nonBlankValidator: NonBlankValidator,
    private val latitudeValidator: LatitudeValidator,
    private val longitudeValidator: LongitudeValidator
) : Validator<Warehouse, WarehouseValidationError> {

    override fun validate(
        value: Warehouse
    ): ValidationResult<WarehouseValidationError> {

        val errors = listOfNotNull(
            idValidator.validate(value.id)
                .toError(WarehouseValidationError.InvalidId),

            nonBlankValidator.validate(value.name)
                .toError(WarehouseValidationError.InvalidName),

            nonBlankValidator.validate(value.regionalZone)
                .toError(WarehouseValidationError.InvalidRegionalZone),

            latitudeValidator.validate(value.latitude)
                .toError(WarehouseValidationError.InvalidLatitude),

            longitudeValidator.validate(value.longitude)
                .toError(WarehouseValidationError.InvalidLongitude)
        )

        return if (errors.isEmpty()) {
            ValidationResult.Valid
        } else {
            ValidationResult.Invalid(errors)
        }
    }
}

private fun <E : ValidationError, T : Any> ValidationResult<E>.toError(
    error: T
): T? =
    when (this) {
        ValidationResult.Valid -> null
        is ValidationResult.Invalid -> error
    }