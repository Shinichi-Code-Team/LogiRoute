package com.example.logiroute.data.repository

import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class CSVVehicleRepository(
    private val loader: Loader,
    private val warehouseRepository: WarehouseRepository
) : VehicleRepository {

    override fun getAllVehicles(): List<Vehicle> {
        val warehouseMap = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return loader.loadFleets().flatMap { raw ->
            val currentHub = warehouseMap[raw.currentHubId]

            if (currentHub == null) {
                println(
                    "Warning: Unknown warehouse reference -> " +
                            "currentHub=${raw.currentHubId}, " +
                            "vehicles=${raw.vehicleIds}"
                )
                emptyList()
            } else {
                raw.vehicleIds.map { vehicleId ->
                    Vehicle(
                        id = vehicleId,
                        maxCapacityKg = raw.maxCapacityKg,
                        costPerKm = raw.costPerKm,
                        currentHub = currentHub
                    )
                }
            }
        }
    }
}