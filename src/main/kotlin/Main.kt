package com.example.logiroute

import com.example.logiroute.data.remote.datasource.SupabaseRouteRemoteDataSource
import com.example.logiroute.data.remote.datasource.SupabaseVehicleRemoteDataSource
import com.example.logiroute.data.remote.datasource.impl.SupabasePackageRemoteDataSource
import com.example.logiroute.data.remote.datasource.impl.SupabaseWarehouseRemoteDataSource
import com.example.logiroute.data.remote.mapper.PackageDtoMapper
import com.example.logiroute.data.remote.mapper.RouteDtoMapper
import com.example.logiroute.data.remote.mapper.VehicleDtoMapper
import com.example.logiroute.data.remote.mapper.WarehouseDtoMapper
import com.example.logiroute.data.repository.PackageRepositoryImpl
import com.example.logiroute.data.repository.RouteRepositoryImpl
import com.example.logiroute.data.repository.VehicleRepositoryImpl
import com.example.logiroute.data.repository.WarehouseRepositoryImpl

suspend fun main() {
    val warehouses = WarehouseRepositoryImpl(
        SupabaseWarehouseRemoteDataSource(),
        WarehouseDtoMapper()
    )

    val routes = RouteRepositoryImpl(
        SupabaseRouteRemoteDataSource(),
        warehouses,
        RouteDtoMapper()
    )

    val packages = PackageRepositoryImpl(
        SupabasePackageRemoteDataSource(),
        warehouses,
        PackageDtoMapper()
    )

    val vehicles = VehicleRepositoryImpl(
        SupabaseVehicleRemoteDataSource(),
        warehouses,
        VehicleDtoMapper()
    )

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