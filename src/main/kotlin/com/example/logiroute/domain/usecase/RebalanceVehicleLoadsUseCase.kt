package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.result.VehicleAssignment

private const val LOW_UTILIZATION_THRESHOLD = 40.0

class RebalanceVehicleLoadsUseCase {

    operator fun invoke(
        assignments: List<VehicleAssignment>
    ): List<VehicleAssignment> {
        val currentAssignments = assignments.associateBy { it.vehicle }.toMutableMap()
        val lowUtilizationAssignments = filterLowUtilizationAssignments(assignments)

        rebalanceLowUtilizationVehicles(lowUtilizationAssignments, currentAssignments)

        return currentAssignments.values.toList()
    }

    private fun filterLowUtilizationAssignments(
        assignments: List<VehicleAssignment>
    ): List<VehicleAssignment> {
        return assignments.filter { calculateUtilization(it) < LOW_UTILIZATION_THRESHOLD }
    }

    private fun calculateUtilization(assignment: VehicleAssignment): Double {
        val projectedLoad = assignment.vehicle.maxCapacityKg - assignment.remainingCapacityKg
        return (projectedLoad / assignment.vehicle.maxCapacityKg) * 100
    }

    private fun rebalanceLowUtilizationVehicles(
        lowAssignments: List<VehicleAssignment>,
        currentAssignments: MutableMap<Vehicle, VehicleAssignment>
    ) {
        lowAssignments.forEach { lowAssignment ->
            if (lowAssignment.vehicle !in currentAssignments) return@forEach

            val otherAssignments = currentAssignments.values
                .filter { it.vehicle != lowAssignment.vehicle }

            val redistributed = redistributePackages(
                packages = lowAssignment.packages,
                assignments = otherAssignments
            )

            if (redistributed != null) {
                applyRedistribution(redistributed, lowAssignment.vehicle, currentAssignments)
            }
        }
    }

    private fun applyRedistribution(
        updatedAssignments: List<VehicleAssignment>,
        removedVehicle: Vehicle,
        currentAssignments: MutableMap<Vehicle, VehicleAssignment>
    ) {
        updatedAssignments.forEach { updated ->
            currentAssignments[updated.vehicle] = updated
        }
        currentAssignments.remove(removedVehicle)
    }

    private fun redistributePackages(
        packages: List<Package>,
        assignments: List<VehicleAssignment>
    ): List<VehicleAssignment>? {
        val updatedAssignments = assignments.associateBy { it.vehicle }.toMutableMap()

        for (packageItem in packages) {
            val bestAssignment = findBestFittingAssignment(updatedAssignments.values, packageItem)
                ?: return null

            updatedAssignments[bestAssignment.vehicle] = assignPackageToVehicle(bestAssignment, packageItem)
        }

        return updatedAssignments.values.toList()
    }

    private fun findBestFittingAssignment(
        candidates: Collection<VehicleAssignment>,
        packageItem: Package
    ): VehicleAssignment? {
        return candidates
            .filter { it.remainingCapacityKg >= packageItem.weight }
            .minByOrNull { it.remainingCapacityKg - packageItem.weight }
    }

    private fun assignPackageToVehicle(
        assignment: VehicleAssignment,
        packageItem: Package
    ): VehicleAssignment {
        return assignment.copy(
            packages = assignment.packages + packageItem,
            totalWeightKg = assignment.totalWeightKg + packageItem.weight,
            remainingCapacityKg = assignment.remainingCapacityKg - packageItem.weight
        )
    }
}