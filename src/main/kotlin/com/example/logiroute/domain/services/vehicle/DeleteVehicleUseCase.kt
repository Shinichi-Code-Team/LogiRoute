package com.example.logiroute.domain.services.vehicle

import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.ValidationRules
import com.example.logiroute.domain.validator.toValidationResult

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
}