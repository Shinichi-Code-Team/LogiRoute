package com.example.logiroute.domain.usecase.crud.vehicle

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.util.suspendRunCatching

class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {
    suspend operator fun invoke(
        vehicle: Vehicle
    ): Result<Vehicle> =
        suspendRunCatching {
            vehicleRepository.addVehicle(vehicle)
        }
}