package com.example.logiroute.domain.services.crud.vehicle

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository

class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {

    suspend operator fun invoke(
        vehicle: Vehicle
    ): Boolean {

        return vehicleRepository.addVehicle(vehicle)
    }
}