package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle


class AssignPackagesToVehiclesUseCase {
    internal companion object {
         const val VEHICLE_RING_SIZE = 100
         val VEHICLE_RING_POSITIONS = listOf(15, 40, 65, 90)
    }
    operator fun invoke(
        packages: List<Package>,
        vehicles: List<Vehicle>
    ): Map<Vehicle, List<Package>> {
        require(packages.isEmpty() || vehicles.isNotEmpty()) {
            "At least one vehicle is required when packages are provided."
        }

        require(vehicles.size <= VEHICLE_RING_POSITIONS.size) {
            "The vehicle ring supports at most ${VEHICLE_RING_POSITIONS.size} vehicles."
        }

        val vehicleSlots = VEHICLE_RING_POSITIONS.zip(vehicles)

        val assignments = packages.groupBy { packageItem ->
            val packageSlot = Math.floorMod(
                packageItem.id.hashCode(),
                VEHICLE_RING_SIZE
            )

            vehicleSlots.firstOrNull { packageSlot <= it.first }?.second
                ?: vehicleSlots.first().second
        }

        return vehicles.associateWith { vehicle ->
            assignments[vehicle].orEmpty()
        }
    }
}
