package com.example.logiroute.domain.usecases

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse

class FindStationedVehiclesByCapacityUseCase {

    fun execute(warehouse: Warehouse, minCapacity: Double): List<Vehicle> {
        if (minCapacity < 0.0) return emptyList()

        return warehouse.stationedVehicles.filter { vehicle -> vehicle.maxCapacityKg >= minCapacity }
    }
}