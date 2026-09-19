package com.example.logiroute.domain.services.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.RouteCreateValidator
import com.example.logiroute.domain.validator.ValidationResult

class CreateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val routeCreateValidator: RouteCreateValidator
) {

    suspend operator fun invoke(
        route: Route
    ): Result<Route> {

        return when (
            val validationResult =
                routeCreateValidator.validate(route)
        ) {
            ValidationResult.Valid -> {
                runCatching {
                    routeRepository.createRoute(route)
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