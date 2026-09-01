package com.example.logiroute.domain.usecase

import com.example.logiroute.com.example.logiroute.domain.model.request.GetWarehouseLoadFactorRequest
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.repository.WarehouseRepository

class GetWarehouseLoadFactorUseCase(
    private val warehouseRepository: WarehouseRepository
) {

    operator fun invoke(request: GetWarehouseLoadFactorRequest): Double {
        val warehouse = warehouseRepository.getAllWarehouses()
            .find { it.id == request.warehouseId }
            ?: throw IllegalArgumentException("Warehouse not found with ID: ${request.warehouseId}")

        val totalCargoWeight = warehouse.cargoQueue
            .sumOf { packageItem -> packageItem.weight }

        val totalFleetCapacity = warehouse.stationedVehicles
            .sumOf { vehicle -> vehicle.maxCapacityKg }

        if (totalFleetCapacity == 0.0) {
            throw LogisticsException.ZeroFleetCapacityException(
                "Cannot calculate load factor for warehouse ${request.warehouseId}: Stationed fleet capacity is zero."
            )
        }

        return totalCargoWeight / totalFleetCapacity
    }
}