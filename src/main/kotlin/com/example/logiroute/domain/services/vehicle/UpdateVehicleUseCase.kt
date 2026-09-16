package com.example.logiroute.domain.services.crud.vehicle

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository

class UpdateVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {

    suspend operator fun invoke(
        vehicle: Vehicle
    ): Boolean {

        return vehicleRepository.updateVehicle(vehicle)
    }
}