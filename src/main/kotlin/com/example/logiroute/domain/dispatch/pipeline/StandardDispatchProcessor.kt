package com.example.logiroute.domain.dispatch.pipeline

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle

class StandardDispatchProcessor : DispatchProcessor() {

    override fun validateCargo(packageItem: Package, vehicle: Vehicle) {
        if (packageItem.weight <= 0.0) {
            throw IllegalArgumentException("Package weight must be positive")
        }
    }

    override fun reserveVehicleCapacity(packageItem: Package, vehicle: Vehicle) {
        if (packageItem.weight > vehicle.maxCapacityKg) {
            throw IllegalStateException("Vehicle capacity exceeded")
        }
    }

    override fun notifyDispatchStatus(packageItem: Package, vehicle: Vehicle) {
        println("STANDARD DISPATCH NOTIFICATION: Package ${packageItem.id} " +
                "has been dispatched using Vehicle ${vehicle.id}.")
    }
}
