package com.example.logiroute.domain.validator.vehicle

import com.example.logiroute.domain.model.request.UpdateVehicleInput
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.PositiveDoubleValidator
import com.example.logiroute.domain.validator.ValidationError
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.Validator
import com.example.logiroute.domain.validator.VehicleValidationError

class VehicleUpdateValidator(
    private val vehicleIdValidator: IdValidator,
    private val warehouseIdValidator: IdValidator,
    private val positiveDoubleValidator: PositiveDoubleValidator
) : Validator<VehicleUpdateValidator.Input, VehicleValidationError> {

    data class Input(
        val id: String?,
        val update: UpdateVehicleInput
    )

    override fun validate(
        value: Input
    ): ValidationResult<VehicleValidationError> {

        val idErrors = listOfNotNull(
            vehicleIdValidator.validate(value.id)
                .toError(VehicleValidationError.InvalidId)
        )

        val fieldErrors = listOfNotNull(
            value.update.maxCapacityKg?.let {
                positiveDoubleValidator.validate(it)
                    .toError(VehicleValidationError.InvalidMaxCapacityKg)
            },

            value.update.costPerKm?.let {
                positiveDoubleValidator.validate(it)
                    .toError(VehicleValidationError.InvalidCostPerKm)
            },

            value.update.currentHubId?.let {
                warehouseIdValidator.validate(it)
                    .toError(VehicleValidationError.InvalidCurrentHubId)
            }
        )

        val noFieldsError =
            if (
                value.update.maxCapacityKg == null &&
                value.update.costPerKm == null &&
                value.update.currentHubId == null
            ) {
                listOf(VehicleValidationError.NoFieldsProvided)
            } else {
                emptyList()
            }

        val errors = idErrors + fieldErrors + noFieldsError

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