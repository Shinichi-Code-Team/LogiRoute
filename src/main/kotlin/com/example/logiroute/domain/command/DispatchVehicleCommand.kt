package com.example.logiroute.domain.command

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.result.VehicleAssignment
import com.example.logiroute.domain.usecase.DispatchVehicleUseCase

class DispatchVehicleCommand(
    private val dispatchVehicleUseCase: DispatchVehicleUseCase,
    private val warehouse: Warehouse,
    private val assignment: VehicleAssignment
) : Command {
    private var previousCargoQueue: List<Package> = emptyList()
    private var previousLoadedPackages: List<Package> = emptyList()
    private var wasExecuted = false

    override fun execute() {
        if (wasExecuted) return
        previousCargoQueue = warehouse.cargoQueue.toList()
        previousLoadedPackages = assignment.vehicle.loadedPackages.toList()
        dispatchVehicleUseCase(warehouse, assignment)
        wasExecuted = true
    }

    override fun undo() {
        if (!wasExecuted) return
        warehouse.restoreCargoQueue(previousCargoQueue)
        assignment.vehicle.loadedPackages.clear()
        assignment.vehicle.loadedPackages.addAll(previousLoadedPackages)
        wasExecuted = false
    }
}