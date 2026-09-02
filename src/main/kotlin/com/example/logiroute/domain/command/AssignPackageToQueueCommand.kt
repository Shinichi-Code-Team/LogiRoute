package com.example.logiroute.domain.command

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.AssignPackageToCargoQueueUseCase

class AssignPackageToQueueCommand(
    private val assignPackageToCargoQueueUseCase: AssignPackageToCargoQueueUseCase,
    private val warehouse: Warehouse,
    private val packageItem: Package
) : Command {
    override fun execute() {
        assignPackageToCargoQueueUseCase(
            warehouse,
            packageItem
        )
    }

    override fun undo() {
        warehouse.removePackage(packageItem)
    }

}