package com.example.logiroute.domain.dispatch.pipeline

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle

class ExpressDispatchProcessor : DispatchProcessor() {

    override fun validateCargo(packageItem: Package, vehicle: Vehicle) {
        if (packageItem.priority != Priority.URGENT) {
            throw IllegalArgumentException("Express processor requires URGENT priority package")
        }
        if (packageItem.weight <= 0.0) {
            throw IllegalArgumentException("Package weight must be positive")
        }
    }

    override fun reserveVehicleCapacity(packageItem: Package, vehicle: Vehicle) {
        if (packageItem.weight > vehicle.maxCapacityKg) {
            throw IllegalStateException("Vehicle capacity exceeded for express dispatch")
        }
    }

    override fun notifyDispatchStatus(packageItem: Package, vehicle: Vehicle) {
        println("EXPRESS DISPATCH NOTIFICATION: Package ${packageItem.id} " +
                "has been dispatched with high priority using Vehicle ${vehicle.id}.")
    }
}
