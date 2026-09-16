package com.example.logiroute.domain.services.crud.vehicle

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository

class ReadVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {

    suspend operator fun invoke(
        id: String
    ): Vehicle? {

        return vehicleRepository.getVehicleById(id)
    }
}