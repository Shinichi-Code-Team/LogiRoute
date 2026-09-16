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

    suspend operator fun invoke(request: ExecuteEmergencyCargoPrioritizationRequest): EmergencyDispatchPlan {
        val vehicle = request.opportunity.availableVehicle
        val urgentPackage = request.opportunity.urgentPackage
        val currentVehiclePackages = fetchCurrentVehiclePackages(request.opportunity.currentWarehouse.id)

        val (currentUrgent, currentLowPriority) = partitionPackagesByPriority(currentVehiclePackages)
        val currentUrgentWeight = calculateTotalWeight(currentUrgent)
        val availableCapacityForUrgent = vehicle.maxCapacityKg - currentUrgentWeight

        val (loadedPackages, offloadedPackages) = resolveCargoLoading(
            urgentPackage = urgentPackage,
            availableCapacityForUrgent = availableCapacityForUrgent,
            currentUrgent = currentUrgent,
            currentLowPriority = currentLowPriority,
            currentUrgentWeight = currentUrgentWeight,
            maxCapacityKg = vehicle.maxCapacityKg
        )

        return buildEmergencyDispatchPlan(vehicle, loadedPackages, offloadedPackages)
    }

    private suspend fun fetchCurrentVehiclePackages(warehouseId: String): List<Package> {
        return packageRepository.getAllPackages()
            .filter { it.origin.id == warehouseId }
    }

    private fun partitionPackagesByPriority(packages: List<Package>): Pair<List<Package>, List<Package>> {
        return packages.partition { it.priority == Priority.URGENT }
    }

    private fun calculateTotalWeight(packages: List<Package>): Double {
        return packages.fold(0.0) { acc, pkg -> acc + pkg.weight }
    }

    private fun resolveCargoLoading(
        urgentPackage: Package,
        availableCapacityForUrgent: Double,
        currentUrgent: List<Package>,
        currentLowPriority: List<Package>,
        currentUrgentWeight: Double,
        maxCapacityKg: Double
    ): Pair<List<Package>, List<Package>> {
        return if (urgentPackage.weight <= availableCapacityForUrgent) {
            Pair(currentUrgent + currentLowPriority + urgentPackage, emptyList())
        } else {
            offloadLowPriorityPackages(urgentPackage, currentUrgent, currentLowPriority, currentUrgentWeight, maxCapacityKg)
        }
    }

    private fun offloadLowPriorityPackages(
        urgentPackage: Package,
        currentUrgent: List<Package>,
        currentLowPriority: List<Package>,
        currentUrgentWeight: Double,
        maxCapacityKg: Double
    ): Pair<List<Package>, List<Package>> {
        val initialAcc = Pair(emptyList<Package>(), emptyList<Package>())
        val (retainedLow, droppedLow) = currentLowPriority.fold(initialAcc) { acc, pkg ->
            val (kept, dropped) = acc
            val currentWeight = currentUrgentWeight + calculateTotalWeight(kept)
            if (currentWeight + urgentPackage.weight + pkg.weight <= maxCapacityKg) {
                Pair(kept + pkg, dropped)
            } else {
                Pair(kept, dropped + pkg)
            }
        }
        return Pair(currentUrgent + retainedLow + urgentPackage, droppedLow)
    }

    private fun buildEmergencyDispatchPlan(
        vehicle: Vehicle,
        loadedPackages: List<Package>,
        offloadedPackages: List<Package>
    ): EmergencyDispatchPlan {
        val totalWeight = calculateTotalWeight(loadedPackages)
        return EmergencyDispatchPlan(
            vehicle = vehicle,
            loadedUrgentPackages = loadedPackages.filter { it.priority == Priority.URGENT },
            offloadedLowPriorityPackages = offloadedPackages,
            totalWeight = totalWeight,
            remainingCapacity = vehicle.maxCapacityKg - totalWeight
        )
    }
}