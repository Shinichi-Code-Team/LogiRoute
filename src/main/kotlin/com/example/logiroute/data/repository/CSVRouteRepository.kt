package com.example.logiroute.data.repository

import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class CSVRouteRepository(
    private val loader: Loader,
    private val warehouseRepository: WarehouseRepository
) : RouteRepository {

    override fun getAllRoutes(): List<Route> {
        val warehouseMap = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return loader.loadRoutes().mapNotNull { raw ->
            val origin = warehouseMap[raw.originHubId]
            val destination = warehouseMap[raw.destinationHubId]

            if (origin == null || destination == null) {
                null
            } else {
                Route(
                    id = raw.id,
                    distanceKm = raw.distanceKm,
                    typicalDelayMin = raw.typicalDelayMin,
                    origin = origin,
                    destination = destination
                )
            }
        }
    }
}