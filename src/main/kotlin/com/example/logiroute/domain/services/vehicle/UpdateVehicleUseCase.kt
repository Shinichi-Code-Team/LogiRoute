package com.example.logiroute.domain.services.vehicle

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.request.UpdateVehicleInput
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.VehicleUpdateValidator

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val vehicleUpdateValidator: VehicleUpdateValidator
) {

    suspend operator fun invoke(
        id: String,
        input: UpdateVehicleInput
    ): Result<Vehicle> {

        val validationInput = VehicleUpdateValidator.Input(
            id = id,
            update = input
        )

        return when (
            val validationResult =
                vehicleUpdateValidator.validate(validationInput)
        ) {
            ValidationResult.Valid -> {
                runCatching {
                    vehicleRepository.updateVehicle(
                        id = id,
                        input = input
                    )
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