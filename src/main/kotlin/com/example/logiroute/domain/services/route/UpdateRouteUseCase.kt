package com.example.logiroute.domain.services.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.repository.RouteRepository

class UpdateRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(
        id: String,
        route: Route
    ): Route {

        return routeRepository.updateRoute(id, route)
    }
}