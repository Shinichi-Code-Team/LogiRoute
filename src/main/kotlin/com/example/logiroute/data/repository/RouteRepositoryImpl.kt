package com.example.logiroute.data.repository

import com.example.logiroute.data.remote.datasource.RemoteRouteDataSource
import com.example.logiroute.data.remote.dto.route.RouteResponseDto
import com.example.logiroute.data.remote.mapper.RouteDtoMapper
import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.UpdateRouteInput
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class RouteRepositoryImpl(
    private val remoteDataSource: RemoteRouteDataSource,
    private val warehouseRepository: WarehouseRepository,
    private val dtoMapper: RouteDtoMapper
) : RouteRepository {

    override suspend fun getAllRoutes(): List<Route> {
        val warehouseMap = getWarehouseMap()

        return remoteDataSource
            .getRoutes()
            .mapNotNull { dto ->
                mapRemoteRoute(dto, warehouseMap)
            }
    }

    override suspend fun getRouteById(id: String): Route? {
        val dto = remoteDataSource.getRouteById(id)
            ?: return null

        return mapRemoteRoute(
            dto = dto,
            warehouseMap = getWarehouseMap()
        )
    }

    override suspend fun createRoute(route: Route): Route {
        val request = dtoMapper.toCreateRequest(route)
        val dto = remoteDataSource.createRoute(request)

        return mapRemoteRoute(
            dto = dto,
            warehouseMap = getWarehouseMap()
        ) ?: error("Unable to resolve route warehouse references")
    }

    override suspend fun updateRoute(
        id: String,
        input: UpdateRouteInput
    ): Route {

        val request = dtoMapper.toUpdateRequest(input)

        val dto = remoteDataSource.updateRoute(
            id = id,
            request = request
        )

        return mapRemoteRoute(
            dto = dto,
            warehouseMap = getWarehouseMap()
        ) ?: error("Unable to resolve route warehouse references")
    }

    override suspend fun deleteRoute(id: String) {
        remoteDataSource.deleteRoute(id)
    }

    private suspend fun getWarehouseMap(): Map<String, Warehouse> {
        return warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }
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