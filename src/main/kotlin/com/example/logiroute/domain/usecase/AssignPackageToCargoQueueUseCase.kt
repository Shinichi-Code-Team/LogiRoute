package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse

class AssignPackageToCargoQueueUseCase {
    operator fun invoke(
        warehouse: Warehouse,
        packageItem: Package
    ): Boolean {
        return warehouse.addPackage(packageItem)

    }

}