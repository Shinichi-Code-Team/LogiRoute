package com.example.logiroute.domain.services.route

import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.ValidationResult

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository,
    private val idValidator: IdValidator
) {

    suspend operator fun invoke(
        id: String
    ): Result<Unit> {

        return when (
            val validationResult = idValidator.validate(id)
        ) {
            ValidationResult.Valid -> {
                runCatching {
                    routeRepository.deleteRoute(id)
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