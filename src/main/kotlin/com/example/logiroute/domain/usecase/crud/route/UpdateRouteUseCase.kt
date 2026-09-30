package com.example.logiroute.domain.services.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.request.UpdateRouteInput
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.validator.RouteUpdateValidator
import com.example.logiroute.domain.validation.ValidationResult

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val routeUpdateValidator: RouteUpdateValidator
) {

    suspend operator fun invoke(
        id: String,
        input: UpdateRouteInput
    ): Result<Route> {

        val validationInput = RouteUpdateValidator.inputValidator(
            id = id,
            update = input
        )

        return when (
            val validationResult =
                routeUpdateValidator.validate(validationInput)
        ) {
            ValidationResult.Valid -> {
                runCatching {
                    routeRepository.updateRoute(
                        id = id,
                        input = input
                    )
                }
            }

            is ValidationResult.Invalid -> {
                Result.failure(
                    LogisticsException.EntityValidationException(
                        validationResult.errors
                    )
                )
            }
        }
    }
}