package com.example.logiroute.domain.usecase.crud.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.util.suspendRunCatching

class CreateRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(
        route: Route
    ): Result<Route> =
        suspendRunCatching {
            routeRepository.createRoute(route)
        }
}