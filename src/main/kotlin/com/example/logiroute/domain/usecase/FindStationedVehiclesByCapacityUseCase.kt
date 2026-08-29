package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository

class InvalidCapacityException(message: String) : IllegalArgumentException(message)

data class FindStationedVehiclesRequest(
    val warehouseId: String,
    val minCapacity: Double
)

class FindStationedVehiclesByCapacityUseCase(
    private val vehicleRepository: VehicleRepository
) {

    operator fun invoke(request: FindStationedVehiclesRequest): List<Vehicle> {
        if (request.minCapacity <= 0) {
            throw InvalidCapacityException("Capacity threshold must be greater than zero. Provided: ${request.minCapacity}")
        }
        return vehicleRepository.getAllVehicles()
            .filter { vehicle -> vehicle.currentHub.id == request.warehouseId && vehicle.maxCapacityKg >= request.minCapacity }
    }
}