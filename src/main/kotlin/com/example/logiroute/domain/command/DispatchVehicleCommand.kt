package com.example.logiroute.domain.command

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.VehicleAssignment
import com.example.logiroute.domain.usecase.DispatchVehicleUseCase
import com.example.logiroute.domain.usecase.TraceHubLineageUseCase

class DispatchVehicleCommand(
    private val dispatchVehicleUseCase: DispatchVehicleUseCase,
    private val warehouse: Warehouse,
    private val assignment: VehicleAssignment
) : Command {

    override fun execute() {
        dispatchVehicleUseCase(
            warehouse,
            assignment
        )
    }

    override fun undo() {
        assignment.packages.forEach { packageItem ->
            assignment.vehicle.loadedPackages.remove(packageItem)
            warehouse.addPackage(packageItem)
        }
    }
}
