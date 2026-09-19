package com.example.logiroute.domain.algorithm

import com.example.logiroute.domain.model.dispatch.DispatchResult
import com.example.logiroute.domain.model.dispatch.VehicleCoverage

class GreedyFleetDispatcher {
    fun dispatch(
        targetZones: Set<String>,
        vehicles: List<VehicleCoverage>
    ): DispatchResult {
        return selectVehicles(
            remainingZones = targetZones,
            remainingVehicles = vehicles,
            selectedVehicleIds = emptyList(),
            coveredZones = emptySet()
        )
    }

    private tailrec fun selectVehicles(
        remainingZones: Set<String>,
        remainingVehicles: List<VehicleCoverage>,
        selectedVehicleIds: List<String>,
        coveredZones: Set<String>
    ): DispatchResult {

        val bestVehicle = remainingVehicles.maxByOrNull { vehicle ->
            vehicle.coveredZones.count { zone -> zone in remainingZones }
        }

        val newlyCovered = bestVehicle
            ?.coveredZones
            ?.intersect(remainingZones)
            .orEmpty()

        val noMoreProgressPossible = remainingZones.isEmpty() ||
                bestVehicle == null ||
                newlyCovered.isEmpty()

        return if (noMoreProgressPossible) {
            DispatchResult(
                selectedVehicleIds = selectedVehicleIds,
                coveredZones = coveredZones,
                uncoveredZones = remainingZones
            )
        } else {
            selectVehicles(
                remainingZones = remainingZones - newlyCovered,
                remainingVehicles = remainingVehicles - bestVehicle,
                selectedVehicleIds = selectedVehicleIds + bestVehicle.vehicleId,
                coveredZones = coveredZones + newlyCovered
            )
        }
    }
}