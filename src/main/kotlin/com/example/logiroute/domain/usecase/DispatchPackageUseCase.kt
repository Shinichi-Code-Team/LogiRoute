package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle

class DispatchPackageUseCase {

    private val standardProcessor = StandardDispatchProcessor()
    private val expressProcessor = ExpressDispatchProcessor()

    operator fun invoke(
        packageItem: Package,
        vehicle: Vehicle
    ): Result<Package> = runCatching {
        val processor = when (packageItem.priority) {
            Priority.URGENT -> expressProcessor
            Priority.LOW,
            Priority.STANDARD -> standardProcessor
        }

        processor.dispatch(packageItem, vehicle)
        packageItem.startTransit()
        packageItem.deliver()

        packageItem
    }
}

private abstract class DispatchProcessor {

    fun dispatch(
        packageItem: Package,
        vehicle: Vehicle
    ) {
        validateCargo(packageItem, vehicle)
        reserveVehicleCapacity(packageItem, vehicle)
        packageItem.assignToVehicle()
    }

    protected abstract fun validateCargo(
        packageItem: Package,
        vehicle: Vehicle
    )

    protected abstract fun reserveVehicleCapacity(
        packageItem: Package,
        vehicle: Vehicle
    )
}

private class StandardDispatchProcessor : DispatchProcessor() {

    override fun validateCargo(
        packageItem: Package,
        vehicle: Vehicle
    ) {
        require(packageItem.weight > 0.0) {
            "Package weight must be positive"
        }
    }

    override fun reserveVehicleCapacity(
        packageItem: Package,
        vehicle: Vehicle
    ) {
        check(packageItem.weight <= vehicle.maxCapacityKg) {
            "Vehicle capacity exceeded"
        }
    }
}

private class ExpressDispatchProcessor : DispatchProcessor() {

    override fun validateCargo(
        packageItem: Package,
        vehicle: Vehicle
    ) {
        require(packageItem.priority == Priority.URGENT) {
            "Express processor requires URGENT priority package"
        }

        require(packageItem.weight > 0.0) {
            "Package weight must be positive"
        }
    }

    override fun reserveVehicleCapacity(
        packageItem: Package,
        vehicle: Vehicle
    ) {
        check(packageItem.weight <= vehicle.maxCapacityKg) {
            "Vehicle capacity exceeded for express dispatch"
        }
    }
}