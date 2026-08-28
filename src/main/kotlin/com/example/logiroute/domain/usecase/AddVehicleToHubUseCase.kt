package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository

class AddVehicleToHubUseCase(
    private val vehicleRepository: VehicleRepository

) {
    operator fun invoke(vehicle: Vehicle): Boolean {
        return vehicleRepository.addVehicle(vehicle)
    }

}