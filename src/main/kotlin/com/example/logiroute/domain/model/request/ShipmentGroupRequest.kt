package com.example.logiroute.domain.model.request

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse

data class ShipmentGroupRequest(
    val packages: List<Package>,
    val origin: Warehouse,
    val destination: Warehouse,
    val service: ShipmentService
)