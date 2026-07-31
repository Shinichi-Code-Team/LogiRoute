package com.example.logiroute.domain.builder


import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.data.dataholder.WarehouseRaw
import com.example.logiroute.data.dataholder.PackageRaw
import com.example.logiroute.data.dataholder.RouteRaw
import com.example.logiroute.data.dataholder.FleetRaw
import com.example.logiroute.data.dataholder.PriorityRaw
import com.example.logiroute.domain.model.Priority

object DomainGraphBuilder {

    fun build(bundle: RawDataBundle): List<Warehouse> {

        val warehouses = createEmptyWarehouses(bundle.warehouses)

        val warehouseById = warehouses.associateBy { it.id }

        val packages = createPackages(bundle.packages, warehouseById)
        val vehicles = createVehicles(bundle.fleet, warehouseById)
        val routes = createRoutes(bundle.routes, warehouseById)

        stitchGraph(warehouses, packages, vehicles, routes)

        return warehouses
    }


    private fun createEmptyWarehouses(rawList: List<WarehouseRaw>): List<Warehouse> {
        return rawList.map { raw ->
            Warehouse(id = raw.id, name = raw.name, regionalZone = raw.regionalZone,latitude = raw.latitude,
                longitude = raw.longitude)
        }
    }

    private fun createPackages(rawList: List<PackageRaw>, warehouseById: Map<String, Warehouse>): List<Package> {
        return rawList.map { raw ->
            Package(
                id = raw.id,
                weight = raw.weight,
                priority = when (raw.priority) {
                    PriorityRaw.LOW -> Priority.LOW
                    PriorityRaw.STANDARD -> Priority.STANDARD
                    PriorityRaw.URGENT -> Priority.URGENT
                },
                origin = warehouseById.getValue(raw.originHubId),
                destination = warehouseById.getValue(raw.destinationHubId)
            )
        }
    }

    private fun createVehicles(rawList: List<FleetRaw>, warehouseById: Map<String, Warehouse>): List<Vehicle> {
        return rawList.map { raw ->
            Vehicle(
                vehicleId = raw.vehicleIds.first(),
                maxCapacityKg = raw.maxCapacityKg,
                costPerKm = raw.costPerKm,
                currentHub = warehouseById.getValue(raw.currentHubId)
            )
        }
    }

    private fun createRoutes(rawList: List<RouteRaw>, warehouseById: Map<String, Warehouse>): List<Route> {
        return rawList.map { raw ->
            Route(
                routeId = raw.routeId,
                distanceKm = raw.distanceKm,
                typicalDelayMin = raw.typicalDelayMin,
                origin = warehouseById.getValue(raw.originHubId),
                destination = warehouseById.getValue(raw.destinationHubId)
            )
        }
    }

    private fun stitchGraph(
        warehouses: List<Warehouse>,
        packages: List<Package>,
        vehicles: List<Vehicle>,
        routes: List<Route>
    ) {
        val packagesByDestination = packages.groupBy { it.destination.id }
        val routesByOrigin = routes.groupBy { it.origin.id }
        val vehiclesByHub = vehicles.groupBy { it.currentHub.id }

        warehouses.forEach { warehouse ->
            packagesByDestination[warehouse.id]?.forEach { warehouse.addPackage(it) }
            routesByOrigin[warehouse.id]?.forEach { warehouse.addRoute(it) }
            vehiclesByHub[warehouse.id]?.forEach { warehouse.addVehicle(it) }
        }
    }
}