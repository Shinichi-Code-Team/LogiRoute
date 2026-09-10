package com.example.logiroute.data.repository

import com.example.logiroute.data.datasource.RouteDataSource
import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class RouteRepositoryImpl(
    private val routeDataSource: RouteDataSource,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    override fun getAllRoutes(): List<Route> {
        val warehouseMap = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return routeDataSource.getRoutes().mapNotNull { raw ->
            val origin = warehouseMap[raw.originHubId]
            val destination = warehouseMap[raw.destinationHubId]

            origin?.let { validOrigin ->
                destination?.let { validDestination ->

                    val route = Route(
                        id = raw.id,
                        distanceKm = raw.distanceKm,
                        typicalDelayMin = raw.typicalDelayMin,
                        origin = validOrigin,
                        destination = validDestination
                    )

                    validOrigin.addOutgoingRoute(route)

                    route
                }
            }
        }
    }
}