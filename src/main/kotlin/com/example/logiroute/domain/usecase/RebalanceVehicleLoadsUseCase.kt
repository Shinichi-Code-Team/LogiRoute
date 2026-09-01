package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.result.VehicleAssignment

private const val LOW_UTILIZATION_THRESHOLD = 40.0

class RebalanceVehicleLoadsUseCase {

    operator fun invoke(
        assignments: List<VehicleAssignment>
    ): List<VehicleAssignment> {

        val currentAssignments =
            assignments
                .associateBy {
                    it.vehicle
                }
                .toMutableMap()

        val lowUtilizationAssignments =
            assignments.filter { assignment ->
                calculateUtilization(
                    assignment
                ) < LOW_UTILIZATION_THRESHOLD
            }

        lowUtilizationAssignments.forEach {
                lowAssignment ->

            if (
                lowAssignment.vehicle
                !in currentAssignments
            ) {
                return@forEach
            }

            val otherAssignments =
                currentAssignments.values
                    .filter {
                        it.vehicle !=
                                lowAssignment.vehicle
                    }

            val redistributed =
                redistributePackages(
                    packages =
                        lowAssignment.packages,
                    assignments =
                        otherAssignments
                )

            if (redistributed != null) {

                redistributed.forEach {
                        updatedAssignment ->

                    currentAssignments[
                        updatedAssignment.vehicle
                    ] = updatedAssignment
                }

                currentAssignments.remove(
                    lowAssignment.vehicle
                )
            }
        }

        return currentAssignments
            .values
            .toList()
    }

    private fun calculateUtilization(
        assignment: VehicleAssignment
    ): Double {

        val projectedLoad =
            assignment.vehicle.maxCapacityKg -
                    assignment.remainingCapacityKg

        return (
                projectedLoad /
                        assignment.vehicle.maxCapacityKg
                ) * 100
    }

    private fun redistributePackages(
        packages: List<Package>,
        assignments:
        List<VehicleAssignment>
    ): List<VehicleAssignment>? {

        val updatedAssignments = assignments.associateBy {
                    it.vehicle
                }
                .toMutableMap()

        packages.forEach { packageItem ->

            val bestAssignment = updatedAssignments.values
                    .filter { assignment ->
                        assignment
                            .remainingCapacityKg >=
                                packageItem.weight
                    }
                    .minByOrNull { assignment ->
                        assignment
                            .remainingCapacityKg -
                                packageItem.weight
                    }
                    ?: return null

            updatedAssignments[bestAssignment.vehicle] =
                bestAssignment.copy(

                    packages = bestAssignment.packages + packageItem,
                    totalWeightKg = bestAssignment.totalWeightKg + packageItem.weight,
                    remainingCapacityKg = bestAssignment.remainingCapacityKg - packageItem.weight
                )
        }
        return updatedAssignments
            .values
            .toList()
    }
}