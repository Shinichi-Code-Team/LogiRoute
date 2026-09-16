package com.example.logiroute.domain.services.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.repository.RouteRepository

class CreateRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(
        route: Route
    ): Route {

        return routeRepository.createRoute(route)
    }
}