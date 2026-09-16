package com.example.logiroute.data.repository

import com.example.logiroute.data.remote.datasource.RemoteRouteDataSource
import com.example.logiroute.data.remote.dto.route.CreateRouteRequestDto
import com.example.logiroute.data.remote.dto.route.RouteResponseDto
import com.example.logiroute.data.remote.dto.route.UpdateRouteRequestDto
import com.example.logiroute.data.remote.mapper.RouteDtoMapper
import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class RouteRepositoryImpl(
    private val remoteDataSource: RemoteRouteDataSource,
    private val warehouseRepository: WarehouseRepository,
    private val dtoMapper: RouteDtoMapper
) : RouteRepository {

    override suspend fun getAllRoutes(): List<Route> {
        val warehouseMap = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return remoteDataSource
            .getRoutes()
            .mapNotNull { dto ->
                mapRemoteRoute(dto, warehouseMap)
            }
    }

    override suspend fun getRouteById(id: String): Route? {
        val dto = remoteDataSource.getRouteById(id)
            ?: return null

        val warehouseMap = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return mapRemoteRoute(dto, warehouseMap)
    }

    override suspend fun createRoute(route: Route): Route {
        val request = CreateRouteRequestDto(
            id = route.id,
            originHubId = route.origin.id,
            destinationHubId = route.destination.id,
            distanceKm = route.distanceKm,
            typicalDelayMin = route.typicalDelayMin
        )

        val dto = remoteDataSource.createRoute(request)

        return dtoMapper.toDomain(
            dto = dto,
            origin = route.origin,
            destination = route.destination
        )
    }

    override suspend fun updateRoute(
        id: String,
        route: Route
    ): Route {
        val request = UpdateRouteRequestDto(
            originHubId = route.origin.id,
            destinationHubId = route.destination.id,
            distanceKm = route.distanceKm,
            typicalDelayMin = route.typicalDelayMin
        )

        val dto = remoteDataSource.updateRoute(id, request)

        return dtoMapper.toDomain(
            dto = dto,
            origin = route.origin,
            destination = route.destination
        )
    }

    override suspend fun deleteRoute(id: String) {
        remoteDataSource.deleteRoute(id)
    }

    private fun mapRemoteRoute(
        dto: RouteResponseDto,
        warehouseMap: Map<String, Warehouse>
    ): Route? {

        val origin = warehouseMap[dto.originHubId]
            ?: return null

        val destination = warehouseMap[dto.destinationHubId]
            ?: return null

        val route = dtoMapper.toDomain(
            dto = dto,
            origin = origin,
            destination = destination
        )

        origin.addOutgoingRoute(route)

        return route
    }
}