package com.example.logiroute

import com.example.logiroute.di.networkModule
import com.example.logiroute.di.repositoryModule
import com.example.logiroute.di.useCaseModule
import com.example.logiroute.di.validatorModule
import com.example.logiroute.domain.dispatch.pipeline.ExpressDispatchProcessor
import com.example.logiroute.domain.dispatch.pipeline.StandardDispatchProcessor
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.OptimizeCargoByWeightUseCase
import com.example.logiroute.domain.usecase.crud.`package`.ReadPackageUseCase
import com.example.logiroute.domain.usecase.crud.vehicle.ReadVehicleUseCase
import org.koin.core.context.startKoin

suspend fun main(args: Array<String>) {
    if (DEMO_ARGUMENT in args) {
        runCargoOptimizationDemo()
        return
    }

    runExistingApplication()
}

private fun runCargoOptimizationDemo() {
    val optimizer = OptimizeCargoByWeightUseCase()

    val origin = Warehouse(
        id = "WH-901",
        name = "Demo Origin",
        regionalZone = "NORTH",
        latitude = 32.0,
        longitude = 35.0
    )

    val destination = Warehouse(
        id = "WH-902",
        name = "Demo Destination",
        regionalZone = "EAST",
        latitude = 31.9,
        longitude = 35.2
    )

    val packages = listOf(
        Package(
            id = "PKG-000101",
            weight = 2.0,
            origin = origin,
            destination = destination,
            priority = Priority.URGENT,
            volumeM3 = 0.9
        ),
        Package(
            id = "PKG-000102",
            weight = 8.0,
            origin = origin,
            destination = destination,
            priority = Priority.STANDARD,
            volumeM3 = 0.2
        )
    )

    val weightOnlySelection = optimizer(
        packages = packages,
        maxCapacityKg = 10.0
    )

    val weightAndVolumeSelection = optimizer(
        packages = packages,
        maxCapacityKg = 10.0,
        maxVolumeM3 = 1.0
    )

    println("=== 2D Cargo Optimizer Demo ===")
    printPayload("Weight limit only", weightOnlySelection)
    printPayload("Weight and volume limits", weightAndVolumeSelection)
    println("=== End Demo ===")
}

private suspend fun runExistingApplication() {
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

    val optimizer = koin.get<OptimizeCargoByWeightUseCase>()
    val selectedPackages = optimizer(
        packages = packages,
        maxCapacityKg = vehicle.maxCapacityKg
    )

    printPayload("Selected cargo from Supabase", selectedPackages)

    println("\nStarting dispatch...")

    selectedPackages.forEach { packageItem ->
        println(
            "\n${packageItem.id}: " +
                    "${packageItem.state::class.simpleName}"
        )

        val processor = if (packageItem.priority == Priority.URGENT) {
            ExpressDispatchProcessor()
        } else {
            StandardDispatchProcessor()
        }

        processor.dispatch(packageItem, vehicle)
            .onSuccess { dispatchedPackage ->
                println(
                    "${dispatchedPackage.id}: " +
                            "${dispatchedPackage.state::class.simpleName}"
                )

                dispatchedPackage.startTransit()
                println(
                    "${dispatchedPackage.id}: " +
                            "${dispatchedPackage.state::class.simpleName}"
                )

                dispatchedPackage.deliver()
                println(
                    "${dispatchedPackage.id}: " +
                            "${dispatchedPackage.state::class.simpleName}"
                )
            }
            .onFailure { exception ->
                println(
                    "Dispatch failed for ${packageItem.id}: " +
                            exception.message
                )
            }
    }

    println("\nEnd-to-end execution completed.")
}

private fun printPayload(
    label: String,
    packages: List<Package>
) {
    println("\n$label")
    println("Package IDs: ${packages.joinToString { it.id }}")
    println("Total weight: ${packages.sumOf { it.weight }} kg")
    println(
        "Total volume: " +
                "${packages.sumOf { it.volumeM3 ?: 0.0 }} m3"
    )
}

private const val DEMO_ARGUMENT = "--knapsack-demo"
private const val VEHICLE_ID = "TRK-0001"

private val PACKAGE_IDS = listOf("PKG-000001", "PKG-000002", "PKG-000003", "PKG-000004", "PKG-000005"
)