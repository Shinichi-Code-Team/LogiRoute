package com.example.logiroute
import com.example.logiroute.di.networkModule
import com.example.logiroute.di.repositoryModule
import com.example.logiroute.di.useCaseModule
import com.example.logiroute.di.validatorModule
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository
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

    val warehouses = koin.get<WarehouseRepository>()
    val routes = koin.get<RouteRepository>()
    val packages = koin.get<PackageRepository>()
    val vehicles = koin.get<VehicleRepository>()

    runCatching { warehouses.getAllWarehouses() }
        .onSuccess { println("Ready warehouses: ${it.size}") }
        .onFailure { println("Warehouses failed: ${it.message}") }

    runCatching { routes.getAllRoutes() }
        .onSuccess { println("Ready routes: ${it.size}") }
        .onFailure { println("Routes failed: ${it.message}") }

    runCatching { packages.getAllPackages() }
        .onSuccess { println("Ready packages: ${it.size}") }
        .onFailure { println("Packages failed: ${it.message}") }

    runCatching { vehicles.getAllVehicles() }
        .onSuccess { println("Ready vehicles: ${it.size}") }
        .onFailure { println("Vehicles failed: ${it.message}") }
}