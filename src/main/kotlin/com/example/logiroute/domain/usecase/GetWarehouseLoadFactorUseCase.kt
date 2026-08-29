package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.repository.WarehouseRepository

class ZeroFleetCapacityException(message: String) : IllegalArgumentException(message)

data class GetWarehouseLoadFactorRequest(
    val warehouseId: String
)

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
            throw ZeroFleetCapacityException(
                "Cannot calculate load factor for warehouse ${request.warehouseId}: Stationed fleet capacity is zero."
            )
        }

        return totalCargoWeight / totalFleetCapacity
    }
}