package com.example.logiroute.domain.model.request

import com.example.logiroute.domain.model.LastMileWaypoint

data class OptimizeLastMileRouteRequest(
    val origin: LastMileWaypoint,
    val deliveryStops: List<LastMileWaypoint>
)