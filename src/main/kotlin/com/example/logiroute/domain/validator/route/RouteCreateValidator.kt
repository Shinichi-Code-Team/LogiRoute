package com.example.logiroute.domain.validator.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.NonNegativeIntValidator
import com.example.logiroute.domain.validator.PositiveDoubleValidator
import com.example.logiroute.domain.validator.ValidationError
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.Validator

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