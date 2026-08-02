package com.example.logiroute.domain.builder

import com.example.logiroute.data.dataholder.*
import com.example.logiroute.domain.model.*

class DomainGraphBuilder {
    fun build(input: DomainGraphInput): DomainGraph {

        val warehouses = buildWarehouses(input.warehouseRaws)
        val warehouseMap = buildWarehouseIndex(warehouses)

        val validPackageRaws = input.packageRaws.filter { packageRaw ->
            normalizeWarehouseId(packageRaw.originHubId) in warehouseMap &&
                    normalizeWarehouseId(packageRaw.destinationHubId) in warehouseMap
        }

        val validRouteRaws = input.routeRaws.filter { routeRaw ->
            normalizeWarehouseId(routeRaw.originHubId) in warehouseMap &&
                    normalizeWarehouseId(routeRaw.destinationHubId) in warehouseMap
        }

        val validFleetRaws = input.fleetRaws.filter { fleetRaw ->
            normalizeWarehouseId(fleetRaw.currentHubId) in warehouseMap
        }

        val packages = buildPackages(validPackageRaws, warehouseMap)

        val routes = buildRoutes(validRouteRaws, warehouseMap)

        val vehicles = buildVehicles(validFleetRaws, warehouseMap)

        return DomainGraph(
            warehouses,
            packages,
            routes,
            vehicles
        )
    }

    private fun buildWarehouses(warehouseRaws: List<WarehouseRaw>): List<Warehouse> {
        return warehouseRaws.map { raw ->
            Warehouse(
                id = raw.id,
                name = raw.name,
                regionalZone = raw.regionalZone,
                latitude = raw.latitude,
                longitude = raw.longitude
            )
        }
    }

    private fun buildWarehouseIndex(warehouses: List<Warehouse>): Map<String, Warehouse> {
        return warehouses.associateBy { normalizeWarehouseId(it.id) }
    }

    private fun buildPackages(
        packageRaws: List<PackageRaw>,
        warehouseMap: Map<String, Warehouse>
    ): List<Package> {
        val packages = mutableListOf<Package>()
        for (packageRaw in packageRaws) {
            packages.add(buildPackage(packageRaw, warehouseMap))
        }
        return packages
    }

    private fun buildPackage(
        packageRaw: PackageRaw,
        warehouseMap: Map<String, Warehouse>
    ): Package {
        val origin = warehouseMap.getValue(normalizeWarehouseId(packageRaw.originHubId))
        val destination = warehouseMap.getValue(normalizeWarehouseId(packageRaw.destinationHubId))

        val packageDomain = Package(
            id = packageRaw.id,
            weight = packageRaw.weight,
            origin = origin,
            destination = destination,
            priority = mapPriority(packageRaw.priority)
        )

        origin.addPackage(packageDomain)
        return packageDomain
    }

    private fun buildRoutes(
        routeRaws: List<RouteRaw>,
        warehouseMap: Map<String, Warehouse>
    ): List<Route> {
        val routes = mutableListOf<Route>()
        for (routeRaw in routeRaws) {
            routes.add(buildRoute(routeRaw, warehouseMap))
        }
        return routes
    }

    private fun buildRoute(
        routeRaw: RouteRaw,
        warehouseMap: Map<String, Warehouse>
    ): Route {
        val origin = warehouseMap.getValue(normalizeWarehouseId(routeRaw.originHubId))
        val destination = warehouseMap.getValue(normalizeWarehouseId(routeRaw.destinationHubId))
        val routeDomain = Route(
            routeId = routeRaw.routeId,
            origin = origin,
            destination = destination,
            distanceKm = routeRaw.distanceKm,
            typicalDelayMin = routeRaw.typicalDelayMin
        )
        origin.addOutgoingRoute(routeDomain)
        return routeDomain
    }

    private fun buildVehicles(
        fleetRaws: List<FleetRaw>,
        warehouseMap: Map<String, Warehouse>
    ): List<Vehicle> {
        val vehicles = mutableListOf<Vehicle>()
        for (fleetRaw in fleetRaws) {
            vehicles.addAll(buildVehiclesFromFleet(fleetRaw, warehouseMap))
        }
        return vehicles
    }

    private fun buildVehiclesFromFleet(
        fleetRaw: FleetRaw,
        warehouseMap: Map<String, Warehouse>
    ): List<Vehicle> {
        val currentHub = warehouseMap.getValue(normalizeWarehouseId(fleetRaw.currentHubId))
        val createdVehicles = mutableListOf<Vehicle>()
        for (vId in fleetRaw.vehicleIds) {
            val vehicleDomain = Vehicle(
                vehicleId = vId,
                maxCapacityKg = fleetRaw.maxCapacityKg,
                costPerKm = fleetRaw.costPerKm,
                currentHub = currentHub
            )
            currentHub.addVehicle(vehicleDomain)
            createdVehicles.add(vehicleDomain)
        }
        return createdVehicles
    }

    private fun normalizeWarehouseId(id: String): String {
        return id.trim().uppercase()
    }

    private fun mapPriority(priorityRaw: PriorityRaw): Priority {
        return when (priorityRaw) {
            PriorityRaw.LOW -> Priority.LOW
            PriorityRaw.STANDARD -> Priority.STANDARD
            PriorityRaw.URGENT -> Priority.URGENT
        }
    }
}