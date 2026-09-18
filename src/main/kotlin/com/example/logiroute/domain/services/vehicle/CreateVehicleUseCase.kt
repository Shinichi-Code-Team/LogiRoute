package com.example.logiroute.domain.services.crud.vehicle

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.vehicle.VehicleCreateValidator

class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val vehicleCreateValidator: VehicleCreateValidator
) {

    suspend operator fun invoke(
        vehicle: Vehicle
    ): Boolean {

        return when (
            val result = vehicleCreateValidator.validate(vehicle)
        ) {
            ValidationResult.Valid ->
                vehicleRepository.addVehicle(vehicle)

            is ValidationResult.Invalid ->
                throw IllegalArgumentException(
                    result.errors.joinToString()
                )
        }
    }
}