package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.BackhaulPlanRequest

class OptimizeBackhaulUseCase {

    private companion object {
        const val INITIAL_WEIGHT = 0.0
    }

    operator fun invoke(
        vehicle: Vehicle,
        candidates: List<Package>,
        returnPath: List<Warehouse>
    ): BackhaulPlanRequest {

        val selectedPackages = candidates
            .sortedByDescending { it.priority }
            .fold(emptyList<Package>() to INITIAL_WEIGHT) { (selected, currentWeight), pkg ->

                val nextWeight = currentWeight + pkg.weight

                if (nextWeight <= vehicle.maxCapacityKg) {
                    (selected + pkg) to nextWeight
                } else {
                    selected to currentWeight
                }
            }
            .first

        return BackhaulPlanRequest(
            vehicle = vehicle,
            selectedPackages = selectedPackages,
            returnPath = returnPath
        )
    }
}