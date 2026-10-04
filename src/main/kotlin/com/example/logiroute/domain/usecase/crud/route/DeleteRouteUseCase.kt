package com.example.logiroute.domain.usecase.crud.route

import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules

class DeleteRouteUseCase(
    private val routeRepository: RouteRepository,
    private val validationRules: ValidationRules,
    private val validationResultMapper: ValidationResultMapper
) {

    suspend operator fun invoke(id: String): Result<Unit> {
        val errors = listOfNotNull(
            validationRules.validateRouteId(id)
        )
        val validationResult = validationResultMapper.map(errors)

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