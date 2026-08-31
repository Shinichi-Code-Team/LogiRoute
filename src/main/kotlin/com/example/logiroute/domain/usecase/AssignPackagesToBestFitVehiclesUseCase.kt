package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.result.VehicleAssignment

private const val MIN_ACCEPTABLE_UTILIZATION = 70.0
private const val MIN_COST_SAVING_RATIO = 0.25

class AssignPackagesToBestFitVehiclesUseCase(
    private val calculateVehicleUtilizationUseCase:
    CalculateVehicleUtilizationUseCase
) {

    operator fun invoke(
        packages: List<Package>,
        vehicles: List<Vehicle>
    ): List<VehicleAssignment> {

        val assignments = vehicles
            .associateWith {
                emptyList<Package>()
            }
            .toMutableMap()

        val remainingCapacities = vehicles
            .associateWith { vehicle ->
                calculateVehicleUtilizationUseCase(
                    vehicle
                ).remainingCapacityKg
            }
            .toMutableMap()

        packages.forEach { packageItem ->

            val candidates = findCandidateVehicles(
                packageItem = packageItem,
                vehicles = vehicles,
                remainingCapacities =
                    remainingCapacities
            )

            val bestFitVehicle = findBestFitVehicle(
                packageItem = packageItem,
                candidates = candidates,
                remainingCapacities =
                    remainingCapacities
            )
                ?: throw LogisticsException
                    .NoSuitableVehicleException(
                        "No suitable vehicle found for package ${packageItem.id}"
                    )

            val selectedVehicle =
                findCostEfficientAlternative(
                    packageItem = packageItem,
                    bestFitVehicle = bestFitVehicle,
                    candidates = candidates,
                    remainingCapacities =
                        remainingCapacities
                ) ?: bestFitVehicle

            assignments[selectedVehicle] = assignments
                    .getValue(selectedVehicle) +
                        packageItem

            remainingCapacities[selectedVehicle] =
                remainingCapacities
                    .getValue(selectedVehicle) -
                        packageItem.weight
        }

        return buildAssignments(
            assignments = assignments,
            remainingCapacities =
                remainingCapacities
        )
    }

    private fun findCandidateVehicles(
        packageItem: Package,
        vehicles: List<Vehicle>,
        remainingCapacities: Map<Vehicle, Double>
    ): List<Vehicle> {

        return vehicles.filter { vehicle ->
            remainingCapacities
                .getValue(vehicle) >=
                    packageItem.weight
        }
    }

    private fun findBestFitVehicle(
        packageItem: Package,
        candidates: List<Vehicle>,
        remainingCapacities: Map<Vehicle, Double>
    ): Vehicle? {

        return candidates.minByOrNull { vehicle ->

            remainingCapacities
                .getValue(vehicle) -
                    packageItem.weight
        }
    }

    private fun findCostEfficientAlternative(
        packageItem: Package,
        bestFitVehicle: Vehicle,
        candidates: List<Vehicle>,
        remainingCapacities: Map<Vehicle, Double>
    ): Vehicle? {

        val maximumAcceptedCostPerKm =
            bestFitVehicle.costPerKm *
                    (1.0 - MIN_COST_SAVING_RATIO)

        return candidates
            .filter {
                it != bestFitVehicle
            }
            .filter { vehicle ->

                calculateProjectedUtilization(
                    vehicle = vehicle,
                    packageItem = packageItem,
                    remainingCapacity =
                        remainingCapacities
                            .getValue(vehicle)
                ) >= MIN_ACCEPTABLE_UTILIZATION
            }
            .filter { vehicle ->

                vehicle.costPerKm <=
                        maximumAcceptedCostPerKm
            }
            .minByOrNull {
                it.costPerKm
            }
    }

    private fun calculateProjectedUtilization(
        vehicle: Vehicle,
        packageItem: Package,
        remainingCapacity: Double
    ): Double {

        val currentPlannedLoad =
            vehicle.maxCapacityKg -
                    remainingCapacity

        val projectedLoad =
            currentPlannedLoad +
                    packageItem.weight

        return (
                projectedLoad /
                        vehicle.maxCapacityKg
                ) * 100
    }

    private fun buildAssignments(
        assignments:
        Map<Vehicle, List<Package>>,
        remainingCapacities:
        Map<Vehicle, Double>
    ): List<VehicleAssignment> {

        return assignments
            .filterValues {
                it.isNotEmpty()
            }
            .map { (vehicle, packages) ->

                VehicleAssignment(
                    vehicle = vehicle,
                    packages = packages,
                    totalWeightKg =
                        packages.sumOf {
                            it.weight
                        },
                    remainingCapacityKg =
                        remainingCapacities
                            .getValue(vehicle)
                )
            }
    }
}