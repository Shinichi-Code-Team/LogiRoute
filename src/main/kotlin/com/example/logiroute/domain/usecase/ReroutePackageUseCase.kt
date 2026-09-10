package com.example.logiroute.com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class ReroutePackageUseCase(
    private val packageRepository: PackageRepository,
    private val warehouseRepository: WarehouseRepository
) {

    operator fun invoke(
        packageId: String,
        newDestinationId: String
    ): Package {
        val packageItem = findPackageById(packageId)
        val newDestination = findWarehouseById(newDestinationId)

        validateRerouteEligibility(packageItem, newDestinationId)
        transferPackageToNewDestination(packageItem, newDestination)

        return createUpdatedPackage(packageItem, newDestination)
    }

    private fun findPackageById(packageId: String): Package {
        return packageRepository.getAllPackages()
            .find { it.id == packageId }
            ?: throw IllegalArgumentException("Package not found: $packageId")
    }

    private fun findWarehouseById(warehouseId: String): Warehouse {
        return warehouseRepository.getAllWarehouses()
            .find { it.id == warehouseId }
            ?: throw IllegalArgumentException("Warehouse not found: $warehouseId")
    }

    private fun validateRerouteEligibility(packageItem: Package, newDestinationId: String) {
        if (packageItem.destination.id == newDestinationId) {
            throw IllegalArgumentException("Package already destined to this warehouse")
        }
    }

    private fun transferPackageToNewDestination(packageItem: Package, newDestination: Warehouse) {
        val removed = packageItem.origin.removePackage(packageItem)
        if (!removed) {
            throw IllegalStateException("Failed to remove package from origin warehouse")
        }
        val updatedPackage = createUpdatedPackage(packageItem, newDestination)
        newDestination.addPackage(updatedPackage)
    }

    private fun createUpdatedPackage(packageItem: Package, newDestination: Warehouse): Package {
        return packageItem.copy(destination = newDestination)
    }
}