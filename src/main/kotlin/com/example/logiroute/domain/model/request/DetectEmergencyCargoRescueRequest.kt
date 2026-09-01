package com.example.logiroute.domain.model.request

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse

data class DetectEmergencyCargoRescueRequest(
    val warehouseId: String
)

data class RescueOpportunity(
    val urgentPackage: Package,
    val currentWarehouse: Warehouse,
    val nextHopWarehouse: Warehouse,
    val availableVehicle: Vehicle
)