package com.example.logiroute.domain.services.route

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.ValidationResult

class ReadRouteUseCase(
    private val routeRepository: RouteRepository,
    private val idValidator: IdValidator
) {

    suspend operator fun invoke(
        id: String
    ): Route? {

        return when (idValidator.validate(id)) {

            ValidationResult.Valid ->
                routeRepository.getRouteById(id)

            is ValidationResult.Invalid ->
                throw IllegalArgumentException("Invalid route ID")
        }
    }
}