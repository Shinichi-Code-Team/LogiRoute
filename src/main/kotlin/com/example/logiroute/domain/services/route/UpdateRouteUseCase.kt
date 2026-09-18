package com.example.logiroute.domain.services.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.request.UpdateRouteInput
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.route.RouteUpdateValidator

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val routeUpdateValidator: RouteUpdateValidator
) {

    suspend operator fun invoke(
        id: String,
        input: UpdateRouteInput
    ): Route {

        val validationInput =
            RouteUpdateValidator.Input(
                id = id,
                update = input
            )

        return when (
            val result = routeUpdateValidator.validate(validationInput)
        ) {

            ValidationResult.Valid ->
                routeRepository.updateRoute(
                    id = id,
                    input = input
                )

            is ValidationResult.Invalid ->
                throw IllegalArgumentException(
                    result.errors.joinToString()
                )
        }
    }
}