package com.example.logiroute.domain.command

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.AssignPackageToCargoQueueUseCase
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import  com.example.logiroute.domain.model.result.AssignmentResult
class AssignPackageToQueueCommand(
    private val assignPackageToCargoQueueUseCase: AssignPackageToCargoQueueUseCase,
    private val warehouse: Warehouse,
    private val packageItem: Package
) : Command {


    override fun execute() {
        val result = assignPackageToCargoQueueUseCase(
            warehouse, packageItem
        )

        if (result != AssignmentResult.Success) {
            throw LogisticsException.CommandExecutionException(packageItem.id)
        }
    }

    override fun undo() {
        warehouse.removePackage(packageItem)

    }

}