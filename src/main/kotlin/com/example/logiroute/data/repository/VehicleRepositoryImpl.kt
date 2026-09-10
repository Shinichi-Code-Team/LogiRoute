package com.example.logiroute.data.repository

import com.example.logiroute.data.dataholder.FleetRaw
import com.example.logiroute.data.datasource.VehicleDataSource
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class VehicleRepositoryImpl(
    private val vehicleDataSource: VehicleDataSource,
    private val warehouseRepository: WarehouseRepository
) : VehicleRepository {

    override fun getAllVehicles(): List<Vehicle> {
        val warehouseMap = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return vehicleDataSource.getFleets().flatMap { raw ->
            val currentHub = warehouseMap[raw.currentHubId]

            currentHub?.let { validCurrentHub ->
                raw.vehicleIds.map { vehicleId ->

                    val vehicle = Vehicle(
                        id = vehicleId,
                        maxCapacityKg = raw.maxCapacityKg,
                        costPerKm = raw.costPerKm,
                        currentHub = validCurrentHub
                    )

                    validCurrentHub.addVehicle(vehicle)

                    vehicle
                }
            } ?: emptyList()
        }
    }

    override fun addVehicle(vehicle: Vehicle): Boolean {
        val fleets = vehicleDataSource.getFleets()

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

        vehicleDataSource.saveFleets(fleets + newFleet)

        return true
    }
}