package com.example.logiroute.data.repository

import com.example.logiroute.data.dataholder.FleetRaw
import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.data.processing.writer.FleetWriter
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class CSVVehicleRepository(
    private val loader: Loader,
    private val writer: FleetWriter,
    private val warehouseRepository: WarehouseRepository
) : VehicleRepository {

    override fun getAllVehicles(): List<Vehicle> {
        val warehouseMap = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return loader.loadFleets().flatMap { raw ->
            val currentHub = warehouseMap[raw.currentHubId]

            if (currentHub == null) {
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
    override fun addVehicle(vehicle: Vehicle): Boolean {
        val fleets = loader.loadFleets()

        val vehicleExists = fleets
            .flatMap { it.vehicleIds }
            .any { it == vehicle.id }

        if (vehicleExists) {
            return false
        }

        val newFleet = FleetRaw(
            vehicleIds = listOf(vehicle.id),
            currentHubId = vehicle.currentHub.id,
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm
        )

        writer.writeFleet(fleets + newFleet)

        return true
    }
}
