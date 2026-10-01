package com.example.logiroute.domain.dispatch.pipeline

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle

abstract class DispatchProcessor {

    fun dispatch(packageItem: Package, vehicle: Vehicle): Result<Package> {
        return try {
            validateCargo(packageItem, vehicle)
            reserveVehicleCapacity(packageItem, vehicle)
            val updatedPackage = updateShipmentState(packageItem)
            notifyDispatchStatus(updatedPackage, vehicle)
            Result.success(updatedPackage)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    protected abstract fun validateCargo(packageItem: Package, vehicle: Vehicle)

    protected abstract fun reserveVehicleCapacity(packageItem: Package, vehicle: Vehicle)

    protected open fun updateShipmentState(packageItem: Package): Package {
        packageItem.assignToVehicle()
        return packageItem
    }

    protected abstract fun notifyDispatchStatus(packageItem: Package, vehicle: Vehicle)
}