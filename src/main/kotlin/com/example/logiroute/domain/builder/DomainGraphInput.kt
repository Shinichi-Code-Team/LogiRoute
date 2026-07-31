package com.example.logiroute.domain.builder

import com.example.logiroute.data.dataholder.*

data class DomainGraphInput(
    val warehouseRaws: List<WarehouseRaw>,
    val packageRaws: List<PackageRaw>,
    val routeRaws: List<RouteRaw>,
    val fleetRaws: List<FleetRaw>,
)
