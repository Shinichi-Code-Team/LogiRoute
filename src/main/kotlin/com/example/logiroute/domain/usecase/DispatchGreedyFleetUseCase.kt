package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.algorithm.GreedyFleetDispatcher
import com.example.logiroute.domain.model.dispatch.DispatchResult
import com.example.logiroute.domain.model.dispatch.VehicleCoverage

class DispatchGreedyFleetUseCase {
    private val dispatcher = GreedyFleetDispatcher()

    operator fun invoke(
        targetZones: Set<String>,
        vehicles: List<VehicleCoverage>
    ): DispatchResult {
        return dispatcher.dispatch(targetZones, vehicles)
    }
}
