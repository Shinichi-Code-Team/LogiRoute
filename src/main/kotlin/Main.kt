package com.example.logiroute

import com.example.logiroute.di.networkModule
import com.example.logiroute.di.repositoryModule
import com.example.logiroute.di.useCaseModule
import com.example.logiroute.di.validatorModule
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.usecase.DispatchPackageUseCase
import com.example.logiroute.domain.usecase.OptimizeCargoByWeightUseCase
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

suspend fun main() {
    val koin = startKoin {
        modules(
            networkModule,
            repositoryModule,
            validatorModule,
            useCaseModule
        )
    }.koin

    try {
        val packageRepository = koin.get<PackageRepository>()
        val vehicleRepository = koin.get<VehicleRepository>()
        val optimizeCargoUseCase = koin.get<OptimizeCargoByWeightUseCase>()
        val dispatchPackageUseCase = koin.get<DispatchPackageUseCase>()

        val vehicle = vehicleRepository.getAllVehicles().firstOrNull()
            ?: run {
                println("No vehicles are available.")
                return
            }

        val packages = packageRepository.getAllPackages()

        if (packages.isEmpty()) {
            println("No packages are available for optimization.")
            return
        }

        println("Vehicle: ${vehicle.id}")
        println("Weight capacity: ${vehicle.maxCapacityKg} kg")
        println("Package pool size: ${packages.size}")

        val selectedPackages = optimizeCargoUseCase(
            packages = packages,
            maxCapacityKg = vehicle.maxCapacityKg
        )

        println("\nStarting dispatch...")

        selectedPackages.forEach { packageItem ->
            dispatchPackageUseCase(packageItem, vehicle)
                .onSuccess { dispatchedPackage ->
                    println(
                        "${dispatchedPackage.id}: " +
                                "${dispatchedPackage.state::class.simpleName}"
                    )
                }
                .onFailure { error ->
                    println(
                        "Failed to dispatch ${packageItem.id}: ${error.message}"
                    )
                }
        }
    } finally {
        stopKoin()
    }
}