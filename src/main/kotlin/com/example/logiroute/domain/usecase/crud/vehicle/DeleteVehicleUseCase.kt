package com.example.logiroute.domain.usecase.crud.vehicle

import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationRules
import com.example.logiroute.domain.validation.toValidationResult

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {

    suspend operator fun invoke(id: String): Result<Unit> {
        val validationResult = listOfNotNull(
            ValidationRules.validateVehicleId(id)
        ).toValidationResult()

        return when (validationResult) {
            ValidationResult.Valid ->
                runCatching {
                    vehicleRepository.deleteVehicle(id)
                    Unit
                }

            is ValidationResult.Invalid ->
                Result.failure(
                    LogisticsException.EntityValidationException(validationResult.errors)
                )
            }
        }
    }
