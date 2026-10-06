package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle


class AssignPackagesToVehiclesUseCase {

    operator fun invoke(
        packages: List<Package>,
        vehicles: List<Vehicle>
    ): Map<Vehicle, List<Package>> {
        require(packages.isEmpty() || vehicles.isNotEmpty()) {
            "At least one vehicle is required when packages are provided."
        }

        require(vehicles.size <= VehicleAssignmentRingConfig.POSITIONS.size) {
            "The vehicle ring supports at most ${VehicleAssignmentRingConfig.POSITIONS.size} vehicles."
        }

        val vehicleSlots = VehicleAssignmentRingConfig.POSITIONS.zip(vehicles)

        val assignments = packages.groupBy { packageItem ->
            val packageSlot = Math.floorMod(
                packageItem.id.hashCode(),
                VehicleAssignmentRingConfig.SIZE
            )

            vehicleSlots.firstOrNull { packageSlot <= it.first }?.second
                ?: vehicleSlots.first().second
        }

        return vehicles.associateWith { vehicle ->
            assignments[vehicle].orEmpty()
        }
    }
}
internal object VehicleAssignmentRingConfig {
    const val SIZE = 100
    val POSITIONS = listOf(15, 40, 65, 90)
}