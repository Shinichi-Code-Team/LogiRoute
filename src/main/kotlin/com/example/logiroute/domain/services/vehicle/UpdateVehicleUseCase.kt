package com.example.logiroute.domain.services.crud.vehicle

import com.example.logiroute.domain.model.request.UpdateVehicleInput
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.vehicle.VehicleUpdateValidator

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val vehicleUpdateValidator: VehicleUpdateValidator
) {

    suspend operator fun invoke(
        id: String,
        input: UpdateVehicleInput
    ): Boolean {

        val validationInput =
            VehicleUpdateValidator.Input(
                id = id,
                update = input
            )

        return when (
            val result = vehicleUpdateValidator.validate(validationInput)
        ) {

            ValidationResult.Valid ->
                vehicleRepository.updateVehicle(
                    id = id,
                    input = input
                )

            is ValidationResult.Invalid ->
                throw IllegalArgumentException(
                    result.errors.joinToString()
                )
        }
    }
}