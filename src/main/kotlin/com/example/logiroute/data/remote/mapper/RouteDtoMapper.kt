package com.example.logiroute.data.remote.mapper

import com.example.logiroute.data.remote.dto.route.CreateRouteRequestDto
import com.example.logiroute.data.remote.dto.route.RouteResponseDto
import com.example.logiroute.data.remote.dto.route.UpdateRouteRequestDto
import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.UpdateRouteInput

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
            distanceKm  = dto.distanceKm,
            typicalDelayMin = dto.typicalDelayMin
        )
    }

    fun toCreateRequest(route: Route): CreateRouteRequestDto {
        return CreateRouteRequestDto(
            id = route.id,
            originHubId = route.origin.id,
            destinationHubId = route.destination.id,
            distanceKm = route.distanceKm,
            typicalDelayMin = route.typicalDelayMin
        )
    }

    fun toUpdateRequest(
        input: UpdateRouteInput
    ): UpdateRouteRequestDto {
        return UpdateRouteRequestDto(
            originHubId = input.originHubId,
            destinationHubId = input.destinationHubId,
            distanceKm = input.distanceKm,
            typicalDelayMin = input.typicalDelayMin
        )
    }
}