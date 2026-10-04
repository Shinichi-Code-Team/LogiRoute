package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.request.EmergencyDispatchPlan
import com.example.logiroute.domain.model.request.ExecuteEmergencyCargoPrioritizationRequest
import com.example.logiroute.domain.repository.PackageRepository

class ExecuteEmergencyCargoPrioritizationUseCase(
    private val packageRepository: PackageRepository
) {

    suspend operator fun invoke(
        request: ExecuteEmergencyCargoPrioritizationRequest
    ): EmergencyDispatchPlan {
        val opportunity = request.opportunity

        val currentPackages = fetchCurrentPackages(
            warehouseId = opportunity.currentWarehouse.id
        )

        return createLoadingPlan(
            vehicle = opportunity.availableVehicle,
            urgentPackage = opportunity.urgentPackage,
            currentPackages = currentPackages
        )
    }

    private suspend fun fetchCurrentPackages(
        warehouseId: String
    ): List<Package> {
        return packageRepository.getAllPackages()
            .filter { packageItem ->
                packageItem.origin.id == warehouseId
            }
    }

    private fun createLoadingPlan(
        vehicle: Vehicle,
        urgentPackage: Package,
        currentPackages: List<Package>
    ): EmergencyDispatchPlan {
        val (urgentPackages, otherPackages) = currentPackages
            .partition { packageItem ->
                packageItem.priority == Priority.URGENT
            }

        val loadedUrgentPackages = urgentPackages + urgentPackage
        val urgentWeight = totalWeight(loadedUrgentPackages)

        check(urgentWeight <= vehicle.maxCapacityKg) {
            "Urgent packages exceed vehicle capacity"
        }

        val remainingCapacity = vehicle.maxCapacityKg - urgentWeight

        val retainedPackages = selectPackagesWithinCapacity(
            packages = otherPackages,
            availableCapacity = remainingCapacity
        )

        val retainedIds = retainedPackages
            .map { packageItem -> packageItem.id }
            .toSet()

        val offloadedPackages = otherPackages.filter { packageItem ->
            packageItem.id !in retainedIds
        }

        val loadedWeight = urgentWeight + totalWeight(retainedPackages)

        return EmergencyDispatchPlan(
            vehicle = vehicle,
            loadedUrgentPackages = loadedUrgentPackages,
            offloadedLowPriorityPackages = offloadedPackages,
            totalWeight = loadedWeight,
            remainingCapacity = vehicle.maxCapacityKg - loadedWeight
        )
    }

    private fun selectPackagesWithinCapacity(
        packages: List<Package>,
        availableCapacity: Double
    ): List<Package> {
        val retainedPackages = mutableListOf<Package>()
        var remainingCapacity = availableCapacity

        for (packageItem in packages) {
            if (packageItem.weight <= remainingCapacity) {
                retainedPackages.add(packageItem)
                remainingCapacity -= packageItem.weight
            }
        }

        return retainedPackages
    }

    private fun totalWeight(packages: List<Package>): Double {
        return packages.sumOf { packageItem -> packageItem.weight }
    }
}