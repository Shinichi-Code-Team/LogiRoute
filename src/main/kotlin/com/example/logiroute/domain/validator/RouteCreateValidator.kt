package com.example.logiroute.domain.validator

import com.example.logiroute.domain.model.Route

class RouteCreateValidator(
    private val routeIdValidator: IdValidator,
    private val warehouseIdValidator: IdValidator,
    private val positiveDoubleValidator: PositiveDoubleValidator,
    private val nonNegativeIntValidator: NonNegativeIntValidator
) : Validator<Route, RouteValidationError> {

    override fun validate(
        value: Route
    ): ValidationResult<RouteValidationError> {

        val errors = listOfNotNull(
            routeIdValidator.validate(value.id)
                .toError(RouteValidationError.InvalidId),

            warehouseIdValidator.validate(value.origin.id)
                .toError(RouteValidationError.InvalidOriginHubId),

            warehouseIdValidator.validate(value.destination.id)
                .toError(RouteValidationError.InvalidDestinationHubId),

            positiveDoubleValidator.validate(value.distanceKm)
                .toError(RouteValidationError.InvalidDistanceKm),

            nonNegativeIntValidator.validate(value.typicalDelayMin)
                .toError(RouteValidationError.InvalidTypicalDelayMin)
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