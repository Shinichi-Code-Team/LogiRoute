package com.example.logiroute.domain.validator.packages

import com.example.logiroute.domain.model.request.UpdatePackageInput
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.PositiveDoubleValidator
import com.example.logiroute.domain.validator.ValidationError
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.Validator
import com.example.logiroute.domain.validator.PackageValidationError

class PackageUpdateValidator(
    private val packageIdValidator: IdValidator,
    private val warehouseIdValidator: IdValidator,
    private val positiveDoubleValidator: PositiveDoubleValidator
) : Validator<PackageUpdateValidator.Input, PackageValidationError> {

    data class Input(
        val id: String?,
        val update: UpdatePackageInput
    )

    override fun validate(
        value: Input
    ): ValidationResult<PackageValidationError> {

        val idErrors = listOfNotNull(
            packageIdValidator.validate(value.id)
                .toError(PackageValidationError.InvalidId)
        )

        val fieldErrors = listOfNotNull(

            value.update.weight?.let {
                positiveDoubleValidator.validate(it)
                    .toError(PackageValidationError.InvalidWeight)
            },

            value.update.originHubId?.let {
                warehouseIdValidator.validate(it)
                    .toError(PackageValidationError.InvalidOriginHubId)
            },

            value.update.destinationHubId?.let {
                warehouseIdValidator.validate(it)
                    .toError(PackageValidationError.InvalidDestinationHubId)
            }
        )

        val noFieldsError =
            if (
                value.update.weight == null &&
                value.update.originHubId == null &&
                value.update.destinationHubId == null &&
                value.update.priority == null
            ) {
                listOf(PackageValidationError.NoFieldsProvided)
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