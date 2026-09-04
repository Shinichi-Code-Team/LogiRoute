package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.result.RouteEvaluationResult

class EstimateDispatchCostUseCase {

    operator fun invoke(
        vehicle: Vehicle,
        routeEvaluation:
        RouteEvaluationResult
    ): Double {

        return routeEvaluation
            .totalDistanceKm *
                vehicle.costPerKm
    }
}