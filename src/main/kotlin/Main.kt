package com.example.logiroute

import com.example.logiroute.di.networkModule
import com.example.logiroute.di.repositoryModule
import com.example.logiroute.di.useCaseModule
import com.example.logiroute.di.validatorModule
import com.example.logiroute.domain.usecase.DispatchPackageUseCase
import com.example.logiroute.domain.usecase.OptimizeCargoByWeightUseCase
import com.example.logiroute.domain.usecase.crud.`package`.ReadPackageUseCase
import com.example.logiroute.domain.usecase.crud.vehicle.ReadVehicleUseCase
import org.koin.core.context.startKoin

suspend fun main() {
    val koin = startKoin {
        modules(
            networkModule,
            repositoryModule,
            validatorModule,
            useCaseModule
        )
    }.koin

    val readPackageUseCase = koin.get<ReadPackageUseCase>()
    val readVehicleUseCase = koin.get<ReadVehicleUseCase>()
    val optimizeCargoPackingUseCase =
        koin.get<OptimizeCargoByWeightUseCase>()
    val dispatchPackageUseCase =
        koin.get<DispatchPackageUseCase>()

    val vehicle = readVehicleUseCase(VEHICLE_ID)
        .getOrElse {
            println("Failed to load vehicle: ${it.message}")
            return
        }
        ?: run {
            println("Vehicle $VEHICLE_ID was not found.")
            return
        }

    val packages = PACKAGE_IDS.mapNotNull { packageId ->
        readPackageUseCase(packageId)
            .onFailure {
                println("Failed to load package $packageId: ${it.message}")
            }
            .getOrNull()
    }

    if (packages.isEmpty()) {
        println("No packages available for optimization.")
        return
    }

    println("Vehicle: ${vehicle.id}")
    println("Capacity: ${vehicle.maxCapacityKg} kg")
    println("Package pool size: ${packages.size}")

    val selectedPackages = optimizeCargoPackingUseCase(
        packages = packages,
        maxCapacityKg = vehicle.maxCapacityKg
    )

    println("\nSelected cargo:")

    selectedPackages.forEach { packageItem ->
        println(
            "${packageItem.id} | " +
                    "Weight: ${packageItem.weight} kg | " +
                    "Priority: ${packageItem.priority}"
        )
    }

    println(
        "Total selected weight: " +
                "${selectedPackages.sumOf { it.weight }} kg"
    )

    println("\nStarting dispatch...")

    selectedPackages.forEach { packageItem ->
        println(
            "\n${packageItem.id}: " +
                    "${packageItem.state::class.simpleName}"
        )
        dispatchPackageUseCase(
            packageItem = packageItem,
            vehicle = vehicle
        ).onSuccess { deliveredPackage ->
            println(
                "${deliveredPackage.id}: " +
                        "${deliveredPackage.state::class.simpleName}"
            )
        }.onFailure { exception ->
            println(
                "Dispatch failed for ${packageItem.id}: " +
                        exception.message
            )
        }
    }

    println("\nEnd-to-end execution completed.")
}

private const val VEHICLE_ID = "TRK-0001"

private val PACKAGE_IDS = listOf("PKG-000001", "PKG-000002", "PKG-000003", "PKG-000004", "PKG-000005"
)