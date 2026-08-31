package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.logic.algorithm.routing.BfsRouter
import com.example.logiroute.domain.logic.algorithm.routing.DijkstraRouter
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.request.ShipmentGroupRequest
import com.example.logiroute.domain.model.request.ShipmentService
import com.example.logiroute.domain.model.result.ShipmentRouteResult

class SelectShipmentRouteUseCase(
    private val distanceRouter: DijkstraRouter,
    private val delayRouter: DijkstraRouter,
    private val bfsRouter: BfsRouter
) {

    operator fun invoke(
        shipment: ShipmentGroupRequest
    ): ShipmentRouteResult {

        val path = selectPath(shipment)
        if (path.isEmpty()) {
            throw LogisticsException
                .RouteNotFoundException(
                    "No route found from " +
                            "${shipment.origin.id} " +
                            "to ${shipment.destination.id}"
                )
        }

        val objective = selectRoutingObjective(shipment.service)
        return ShipmentRouteResult(
            path = path,
            routingObjective = objective
        )
    }

    private fun selectPath(
        shipment: ShipmentGroupRequest
    ): List<Warehouse> {

        return when (shipment.service) {
            ShipmentService.ECO ->
                distanceRouter.findRoute(
                    source = shipment.origin,
                    destination =
                        shipment.destination
                )

            ShipmentService.EXPRESS ->

                delayRouter.findRoute(
                    source = shipment.origin,
                    destination =
                        shipment.destination
                )

            ShipmentService.FRAGILE ->

                bfsRouter.findRoute(
                    source = shipment.origin,
                    destination =
                        shipment.destination
                )
        }
    }

    private fun selectRoutingObjective(
        service: ShipmentService
    ): String {

        return when (service) {
            ShipmentService.ECO ->
                "MIN_DISTANCE"

            ShipmentService.EXPRESS ->
                "MIN_EXPECTED_DELAY"

            ShipmentService.FRAGILE ->
                "MIN_HOPS"
        }
    }
}