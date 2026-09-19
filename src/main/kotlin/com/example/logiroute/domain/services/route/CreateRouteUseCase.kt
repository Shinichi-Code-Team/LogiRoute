package com.example.logiroute.domain.services.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.route.RouteCreateValidator

class CreateRouteUseCase(
    private val routeRepository: RouteRepository,
    private val routeCreateValidator: RouteCreateValidator
) {

    suspend operator fun invoke(
        route: Route
    ): Route {

        return when (val result = routeCreateValidator.validate(route)) {

            ValidationResult.Valid ->
                routeRepository.createRoute(route)

            is ValidationResult.Invalid ->
                throw IllegalArgumentException(
                    result.errors.joinToString()
                )
        }
    }
}