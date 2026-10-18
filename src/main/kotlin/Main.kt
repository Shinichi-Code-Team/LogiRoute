package com.example.logiroute

import com.example.logiroute.di.networkModule
import com.example.logiroute.di.repositoryModule
import com.example.logiroute.di.useCaseModule
import com.example.logiroute.di.validatorModule
import com.example.logiroute.domain.usecase.ClassifyPackageSectorUseCase
import com.example.logiroute.domain.usecase.FindOptimalPathUseCase
import com.example.logiroute.domain.usecase.TrackActiveFleetUseCase
import com.example.logiroute.domain.usecase.crud.vehicle.ReadVehicleUseCase
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

suspend fun main() {
    val koin = startKoin {
        modules(
            networkModule,
            repositoryModule,
            validatorModule,
            useCaseModule,

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
    println("Weight capacity: ${vehicle.maxCapacityKg} kg")
    println("Package pool size: ${packages.size}")

    val selectedPackages = optimizeCargoPackingUseCase(
        packages = packages,
        maxCapacityKg = vehicle.maxCapacityKg
    )

    printPayload("Selected cargo from Supabase", selectedPackages)

    println("\nStartFing dispatch...")

    selectedPackages.forEach { packageItem ->
        println(
            "\n${packageItem.id}: " +
                    "${packageItem.state::class.simpleName}"
        )

        cli.run()
    } finally {
        stopKoin()
    }
}