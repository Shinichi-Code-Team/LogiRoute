package com.example.logiroute.domain.services.route

import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.ValidationRules
import com.example.logiroute.domain.validator.toValidationResult

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(id: String): Result<Unit> {
        val validationResult = listOfNotNull(
            ValidationRules.validateRouteId(id)
        ).toValidationResult()

        return when (validationResult) {
            ValidationResult.Valid ->
                runCatching {
                    routeRepository.deleteRoute(id)
                }

            is ValidationResult.Invalid ->
                Result.failure(
                    LogisticsException.EntityValidationException(validationResult.errors)
                )
        }
    }
}