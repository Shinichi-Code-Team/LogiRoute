package com.example.logiroute.data.csv.mapper

import com.example.logiroute.data.csv.raw.FleetRaw
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse

class VehicleRawMapper {

    fun toDomainList(raw: FleetRaw, currentHub: Warehouse): List<Vehicle> {
        return raw.vehicleIds.map { vehicleId ->
            Vehicle(
                id = vehicleId,
                maxCapacityKg = raw.maxCapacityKg,
                costPerKm = raw.costPerKm,
                currentHub = currentHub
            )
        }
    }
}
