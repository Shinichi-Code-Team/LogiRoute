package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.AssignmentResult

class AssignPackageToCargoQueueUseCase {
    operator fun invoke(
        warehouse: Warehouse,
        packageItem: Package
    ):  AssignmentResult {
        val wasAdded = warehouse.addPackage(packageItem)

        val rules: List<Pair<() -> Boolean, AssignmentResult>> = listOf(
            { wasAdded } to AssignmentResult.Success,
            { warehouse.cargoQueue.any { it.id == packageItem.id } } to AssignmentResult.AlreadyQueued
        )

        return rules.firstOrNull { (isMatch, _) -> isMatch() }?.second
            ?: AssignmentResult.OriginMismatch
    }
}