package com.example.logiroute.domain.model.result

import com.example.logiroute.domain.model.Warehouse

data class ShipmentRouteResult(
    val path: List<Warehouse>,
    val routingObjective: String
)