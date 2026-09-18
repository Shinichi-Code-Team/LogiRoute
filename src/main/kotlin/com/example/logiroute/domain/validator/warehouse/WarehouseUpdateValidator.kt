package com.example.logiroute.domain.validator.warehouse

import com.example.logiroute.domain.model.request.UpdateWarehouseInput
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.LatitudeValidator
import com.example.logiroute.domain.validator.LongitudeValidator
import com.example.logiroute.domain.validator.NonBlankValidator
import com.example.logiroute.domain.validator.ValidationError
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.Validator
import com.example.logiroute.domain.validator.WarehouseValidationError

class WarehouseUpdateValidator(
    private val idValidator: IdValidator,
    private val nonBlankValidator: NonBlankValidator,
    private val latitudeValidator: LatitudeValidator,
    private val longitudeValidator: LongitudeValidator
) : Validator<WarehouseUpdateValidator.Input, WarehouseValidationError> {

    data class Input(
        val id: String?,
        val update: UpdateWarehouseInput
    )

    override fun validate(
        value: Input
    ): ValidationResult<WarehouseValidationError> {

        val idErrors = listOfNotNull(
            idValidator.validate(value.id)
                .toError(WarehouseValidationError.InvalidId)
        )

        val fieldErrors = listOfNotNull(

            value.update.name?.let {
                nonBlankValidator.validate(it)
                    .toError(WarehouseValidationError.InvalidName)
            },

            value.update.regionalZone?.let {
                nonBlankValidator.validate(it)
                    .toError(WarehouseValidationError.InvalidRegionalZone)
            },

            value.update.latitude?.let {
                latitudeValidator.validate(it)
                    .toError(WarehouseValidationError.InvalidLatitude)
            },

            value.update.longitude?.let {
                longitudeValidator.validate(it)
                    .toError(WarehouseValidationError.InvalidLongitude)
            }
        )

        val noFieldsError =
            if (
                value.update.name == null &&
                value.update.regionalZone == null &&
                value.update.latitude == null &&
                value.update.longitude == null
            ) {
                listOf(WarehouseValidationError.NoFieldsProvided)
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