package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.PackageRepository

class FindBackhaulCandidatesUseCase(
    private val packageRepository: PackageRepository
) {

    operator fun invoke(
        vehicle: Vehicle,
        currentHub: Warehouse,
        returnPath: List<Warehouse>
    ): List<Package> {

        val packages = packageRepository.getAllPackages()

        val destinationsOnReturnPath = returnPath.toSet()

        return packages.filter { pkg ->
            pkg.origin == currentHub &&
                    pkg.destination in destinationsOnReturnPath &&
                    pkg.weight <= vehicle.maxCapacityKg
        }
    }
}