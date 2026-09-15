package com.example.logiroute.data.remote.mapper

import com.example.logiroute.data.remote.dto.route.RouteResponseDto
import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse

class RouteDtoMapper {

    fun toDomain(
        dto: RouteResponseDto,
        origin: Warehouse,
        destination: Warehouse
    ): Route {
        return Route(
            id = dto.id,
            origin = origin,
            destination = destination,
            distanceKm = dto.distanceKm,
            typicalDelayMin = dto.typicalDelayMin
        )
    }
}