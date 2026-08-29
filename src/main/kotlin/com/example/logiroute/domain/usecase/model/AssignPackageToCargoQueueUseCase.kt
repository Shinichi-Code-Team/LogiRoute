package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class AssignPackageToCargoQueueUseCase(
    private val packageRepository: PackageRepository,
    private val warehouseRepository: WarehouseRepository
) {
    operator fun invoke(
        packageId: String,
        warehouseId: String
    ): List<Package> {
        val packageToAdd = packageRepository.getAllPackages()
            .find { it.id == packageId }
            ?: throw IllegalArgumentException("Package not found: $packageId")

        val warehouse = warehouseRepository.getAllWarehouses()
            .find { it.id == warehouseId }
            ?: throw IllegalArgumentException("Warehouse not found: $warehouseId")

        warehouse.addPackage(packageToAdd)
        return quickSort(warehouse.cargoQueue)
    }

    private fun quickSort(packages: List<Package>): List<Package> {
        if (packages.size <= 1) return packages

        val pivot = packages.last()
        val (less, greater) = packages.dropLast(1).partition { packageItem ->
            comparePackages(packageItem, pivot) <= 0
        }

        return quickSort(less) + listOf(pivot) + quickSort(greater)
    }

    private fun comparePackages(a: Package, b: Package): Int =
        compareValuesBy(
            a, b,
            { it.priority.ordinal },
            { -it.weight }
        )
}