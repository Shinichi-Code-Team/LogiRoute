package com.example.logiroute.domain.services.crud.vehicle

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.ValidationResult

class ReadVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val idValidator: IdValidator
) {

    suspend operator fun invoke(
        id: String
    ): Vehicle? {

        return when (idValidator.validate(id)) {

            ValidationResult.Valid ->
                vehicleRepository.getVehicleById(id)

            is ValidationResult.Invalid ->
                throw IllegalArgumentException(
                    "Invalid vehicle ID"
                )
        }
    }
}