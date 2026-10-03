package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.AssignmentResult

class AssignPackageToCargoQueueUseCase(
    private val sortCargoQueue: SortCargoQueueByWeightUseCase
) {
    operator fun invoke(
        warehouse: Warehouse,
        packageItem: Package
    ): AssignmentResult {
        val wasAdded = warehouse.addPackage(packageItem)

        if (wasAdded) {
            warehouse.restoreCargoQueue(
                sortCargoQueue(warehouse.cargoQueue)
            )
            return AssignmentResult.Success
        }

        return if (warehouse.cargoQueue.any { it.id == packageItem.id }) {
            AssignmentResult.AlreadyQueued
        } else {
            AssignmentResult.OriginMismatch
        }
    }
}