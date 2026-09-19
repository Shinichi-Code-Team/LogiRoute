package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.Vehicle

class VehicleCreateValidator(
    private val vehicleIdValidator: IdValidator,
    private val warehouseIdValidator: IdValidator,
    private val positiveDoubleValidator: PositiveDoubleValidator
) : Validator<Vehicle, VehicleValidationError> {

    override fun validate(
        value: Vehicle
    ): ValidationResult<VehicleValidationError> {

        val errors = listOfNotNull(
            vehicleIdValidator.validate(value.id)
                .toError(VehicleValidationError.InvalidId),

            warehouseIdValidator.validate(value.currentHub.id)
                .toError(VehicleValidationError.InvalidCurrentHubId),

            positiveDoubleValidator.validate(value.maxCapacityKg)
                .toError(VehicleValidationError.InvalidMaxCapacityKg),

            positiveDoubleValidator.validate(value.costPerKm)
                .toError(VehicleValidationError.InvalidCostPerKm)
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