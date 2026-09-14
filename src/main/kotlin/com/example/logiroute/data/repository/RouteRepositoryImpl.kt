package com.example.logiroute.data.repository

import com.example.logiroute.data.remote.datasource.RemoteRouteDataSource
import com.example.logiroute.data.remote.dto.route.RouteResponseDto
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

    override fun getAllRoutes(): List<Route> {
        val warehouseMap = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return remoteDataSource
            .getRoutes()
            .mapNotNull { dto ->
                mapRemoteRoute(
                    dto = dto,
                    warehouseMap = warehouseMap
                )
            }
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