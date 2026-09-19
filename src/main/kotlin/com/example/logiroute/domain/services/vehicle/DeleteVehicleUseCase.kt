package com.example.logiroute.domain.services.vehicle

import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.ValidationResult

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
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
                    vehicleRepository.deleteVehicle(id)
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