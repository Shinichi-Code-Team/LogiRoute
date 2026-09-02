package com.example.logiroute.domain.usecase

import com.example.logiroute.com.example.logiroute.domain.usecase.model.request.FindStationedVehiclesRequest
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException

class FindStationedVehiclesByCapacityUseCase(
    private val vehicleRepository: VehicleRepository
) {

    operator fun invoke(request: FindStationedVehiclesRequest): List<Vehicle> {
        if (request.minCapacity <= 0.0) {
            throw LogisticsException.InvalidCapacityException(request.minCapacity)
        }
        return vehicleRepository.getAllVehicles()
            .filter { vehicle -> vehicle.currentHub.id == request.warehouseId && vehicle.maxCapacityKg >= request.minCapacity }
    }
}