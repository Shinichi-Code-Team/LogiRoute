package com.example.logiroute.domain.services.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.repository.RouteRepository

class ReadRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(
        id: String
    ): Route? {

        return routeRepository.getRouteById(id)
    }
}