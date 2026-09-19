package com.example.logiroute.domain.validator.route

import com.example.logiroute.domain.model.request.UpdateRouteInput
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.NonNegativeIntValidator
import com.example.logiroute.domain.validator.PositiveDoubleValidator
import com.example.logiroute.domain.validator.ValidationError
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.Validator

class RouteUpdateValidator(
    private val routeIdValidator: IdValidator,
    private val warehouseIdValidator: IdValidator,
    private val positiveDoubleValidator: PositiveDoubleValidator,
    private val nonNegativeIntValidator: NonNegativeIntValidator
) : Validator<RouteUpdateValidator.Input, RouteValidationError> {

    data class Input(
        val id: String?,
        val update: UpdateRouteInput
    )

    override fun validate(
        value: Input
    ): ValidationResult<RouteValidationError> {

        val idErrors = listOfNotNull(
            routeIdValidator.validate(value.id)
                .toError(RouteValidationError.InvalidId)
        )

        val fieldErrors = listOfNotNull(

            value.update.originHubId?.let {
                warehouseIdValidator.validate(it)
                    .toError(RouteValidationError.InvalidOriginHubId)
            },

            value.update.destinationHubId?.let {
                warehouseIdValidator.validate(it)
                    .toError(RouteValidationError.InvalidDestinationHubId)
            },

            value.update.distanceKm?.let {
                positiveDoubleValidator.validate(it)
                    .toError(RouteValidationError.InvalidDistanceKm)
            },

            value.update.typicalDelayMin?.let {
                nonNegativeIntValidator.validate(it)
                    .toError(RouteValidationError.InvalidTypicalDelayMin)
            }
        )

        val noFieldsError =
            if (
                value.update.originHubId == null &&
                value.update.destinationHubId == null &&
                value.update.distanceKm == null &&
                value.update.typicalDelayMin == null
            ) {
                listOf(RouteValidationError.NoFieldsProvided)
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