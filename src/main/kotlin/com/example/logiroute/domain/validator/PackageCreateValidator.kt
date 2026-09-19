package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.Package

class PackageCreateValidator(
    private val packageIdValidator: IdValidator,
    private val warehouseIdValidator: IdValidator,
    private val positiveDoubleValidator: PositiveDoubleValidator
) : Validator<Package, PackageValidationError> {

    override fun validate(
        value: Package
    ): ValidationResult<PackageValidationError> {

        val errors = listOfNotNull(
            packageIdValidator.validate(value.id)
                .toError(PackageValidationError.InvalidId),

            positiveDoubleValidator.validate(value.weight)
                .toError(PackageValidationError.InvalidWeight),

            warehouseIdValidator.validate(value.origin.id)
                .toError(PackageValidationError.InvalidOriginHubId),

            warehouseIdValidator.validate(value.destination.id)
                .toError(PackageValidationError.InvalidDestinationHubId)
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