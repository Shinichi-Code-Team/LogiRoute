package com.example.logiroute.domain.builder

import com.example.logiroute.data.dataholder.FleetRaw
import com.example.logiroute.data.dataholder.PackageRaw
import com.example.logiroute.data.dataholder.RouteRaw
import com.example.logiroute.data.dataholder.WarehouseRaw

data class RawDataBundle(
    val warehouses: List<WarehouseRaw>,
    val packages: List<PackageRaw>,
    val routes: List<RouteRaw>,
    val fleet: List<FleetRaw>
)

