package com.example.logiroute.domain.services.route

import com.example.logiroute.domain.repository.RouteRepository

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(
        id: String
    ) {

        routeRepository.deleteRoute(id)
    }
}