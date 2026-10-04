package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.request.ShipmentGroupRequest
import com.example.logiroute.domain.model.request.ShipmentService
import com.example.logiroute.domain.model.result.ShipmentRouteResult

class SelectShipmentRouteUseCase(
    private val distancePathUseCase: FindOptimalPathUseCase,
    private val delayPathUseCase: FindOptimalPathUseCase,
    private val fewestHopsRouteUseCase: FindFewestHopsRouteUseCase
) {

    suspend operator fun invoke(
        shipment: ShipmentGroupRequest
    ): ShipmentRouteResult {

        val path = selectPath(shipment)

        if (path.isEmpty()) {
            throw LogisticsException.RouteNotFoundException(
                "No route found from ${shipment.origin.id} " +
                        "to ${shipment.destination.id}"
            )
        }

        return ShipmentRouteResult(
            path = path,
            routingObjective = selectRoutingObjective(shipment.service)
        )
    }

    private suspend fun selectPath(
        shipment: ShipmentGroupRequest
    ): List<Warehouse> {

        return when (shipment.service) {
            ShipmentService.ECO ->
                distancePathUseCase(
                    source = shipment.origin,
                    destination = shipment.destination
                )

            ShipmentService.EXPRESS ->
                delayPathUseCase(
                    source = shipment.origin,
                    destination = shipment.destination
                )

            ShipmentService.FRAGILE ->
                fewestHopsRouteUseCase(
                    source = shipment.origin,
                    destination = shipment.destination
                )
        }
    }

    private fun selectRoutingObjective(
        service: ShipmentService
    ): String {

        return when (service) {
            ShipmentService.ECO -> "MIN_DISTANCE"
            ShipmentService.EXPRESS -> "MIN_EXPECTED_DELAY"
            ShipmentService.FRAGILE -> "MIN_HOPS"
        }
    }
}