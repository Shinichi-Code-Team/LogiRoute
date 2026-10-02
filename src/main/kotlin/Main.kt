package com.example.logiroute
import com.example.logiroute.di.networkModule
import com.example.logiroute.di.repositoryModule
import com.example.logiroute.di.useCaseModule
import com.example.logiroute.di.validatorModule
import com.example.logiroute.domain.algorithm.optimization.KnapsackCargoOptimizer
import com.example.logiroute.domain.dispatch.pipeline.ExpressDispatchProcessor
import com.example.logiroute.domain.dispatch.pipeline.StandardDispatchProcessor
import com.example.logiroute.domain.model.Priority
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
                println(
                    "Failed to load package $packageId: ${it.message}"
                )
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

    val optimizer = KnapsackCargoOptimizer()

    val selectedPackages = optimizer.optimize(
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

        val processor =
            if (packageItem.priority == Priority.URGENT) {
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

private const val VEHICLE_ID = "TRK-0001"
private val PACKAGE_IDS = listOf( "PKG-000001", "PKG-000002", "PKG-000003", "PKG-000004", "PKG-000005" )