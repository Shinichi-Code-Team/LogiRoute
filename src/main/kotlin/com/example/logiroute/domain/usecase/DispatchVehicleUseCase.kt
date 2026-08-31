package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.com.example.logiroute.domain.model.request.ConsolidationPlanRequest

class DispatchVehicleUseCase {

    operator fun invoke(
        warehouse: Warehouse,
        plan: ConsolidationPlanRequest
    ): List<Package> {

        plan.vehicle.loadedPackages
            .addAll(plan.selectedPackages)

        plan.selectedPackages.forEach { pkg ->
            warehouse.removePackage(pkg)
        }

        return plan.selectedPackages
    }
}