package com.example.logiroute.domain.builder

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class DomainGraphBuilder(
    private val packageRepository: PackageRepository,
    private val routeRepository: RouteRepository,
    private val warehouseRepository: WarehouseRepository,
    private val vehicleRepository: VehicleRepository
) {

    fun build(): DomainGraph {
        val warehouses = warehouseRepository.getAllWarehouses()
        val packages = packageRepository.getAllPackages()
        val routes = routeRepository.getAllRoutes()
        val vehicles = vehicleRepository.getAllVehicles()

        attachPackagesToWarehouses(packages)
        attachRoutesToWarehouses(routes)
        attachVehiclesToWarehouses(vehicles)

        return DomainGraph(
            warehouses = warehouses,
            packages = packages,
            routes = routes,
            vehicles = vehicles
        )
    }

    private fun attachPackagesToWarehouses(
        packages: List<Package>
    ) {
        packages.forEach { packageItem ->
            packageItem.origin.addPackage(packageItem)
        }
    }

    private fun attachRoutesToWarehouses(
        routes: List<Route>
    ) {
        routes.forEach { route ->
            route.origin.addOutgoingRoute(route)
        }
    }

    private fun attachVehiclesToWarehouses(
        vehicles: List<Vehicle>
    ) {
        vehicles.forEach { vehicle ->
            vehicle.currentHub.addVehicle(vehicle)
        }
    }
}