package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.request.EmergencyDispatchPlan
import com.example.logiroute.domain.model.request.ExecuteEmergencyCargoPrioritizationRequest
import com.example.logiroute.domain.repository.PackageRepository

class ExecuteEmergencyCargoPrioritizationUseCase(
    private val packageRepository: PackageRepository
) {
    operator fun invoke(request: ExecuteEmergencyCargoPrioritizationRequest): EmergencyDispatchPlan {
        val vehicle = request.opportunity.availableVehicle
        val urgentPackage = request.opportunity.urgentPackage

        val currentVehiclePackages = packageRepository.getAllPackages()
            .filter { it.origin.id == request.opportunity.currentWarehouse.id }

        val (currentUrgent, currentLowPriority) = currentVehiclePackages.partition {
            it.priority == Priority.URGENT
        }

        val currentUrgentWeight = currentUrgent.fold(0.0) { acc, pkg -> acc + pkg.weight }
        val availableCapacityForUrgent = vehicle.maxCapacityKg - currentUrgentWeight

        val (loadedPackages, offloadedPackages) = if (urgentPackage.weight <= availableCapacityForUrgent) {
            Pair(currentUrgent + currentLowPriority + urgentPackage, emptyList())
        } else {
            val initialAcc = Pair(emptyList<Package>(), emptyList<Package>())
            val (retainedLow, droppedLow) = currentLowPriority.fold(initialAcc) { acc, pkg ->
                val (kept, dropped) = acc
                val currentWeight = currentUrgentWeight + kept.fold(0.0) { wAcc, p -> wAcc + p.weight }
                if (currentWeight + urgentPackage.weight + pkg.weight <= vehicle.maxCapacityKg) {
                    Pair(kept + pkg, dropped)
                } else {
                    Pair(kept, dropped + pkg)
                }
            }
            Pair(currentUrgent + retainedLow + urgentPackage, droppedLow)
        }

        val totalWeight = loadedPackages.fold(0.0) { acc, pkg -> acc + pkg.weight }

        return EmergencyDispatchPlan(
            vehicle = vehicle,
            loadedUrgentPackages = loadedPackages.filter { it.priority == Priority.URGENT },
            offloadedLowPriorityPackages = offloadedPackages,
            totalWeight = totalWeight,
            remainingCapacity = vehicle.maxCapacityKg - totalWeight
        )
    }
}