package com.example.logiroute.domain.builder

import com.example.logiroute.data.dataholder.*
import com.example.logiroute.domain.model.*

class DomainGraphBuilder {
    class DomainGraphBuilder {

        fun build(input: DomainGraphInput): DomainGraph {
            val warehouses = buildWarehouses(input.warehouseRaws)
            val warehouseMap = buildWarehouseIndex(warehouses)
            val packages = buildPackages(input.packageRaws, warehouseMap)

            // TODO: waiting on Route domain class + RouteRaw parsing from teammate
            val routes = emptyList<Route>()

            // TODO: waiting on Vehicle domain class + VehicleRaw parsing from teammate
            val vehicles = emptyList<Vehicle>()

            return DomainGraph(warehouses, packages, routes, vehicles)
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
            return warehouses.associateBy { it.id }
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
            val origin = warehouseMap.getValue(packageRaw.originHubId)
            val destination = warehouseMap.getValue(packageRaw.destinationHubId)

            val packageDomain = Package(
                id = packageRaw.id,
                weight = packageRaw.weight,
                origin = origin,
                destination = destination,
                priority = packageRaw.priority
            )

            origin.addPackage(packageDomain)
            return packageDomain
        }

        // TODO: implement once Route domain class and RouteRaw are ready
        // private fun buildRoutes(routeRaws: List<RouteRaw>, warehouseMap: Map<String, Warehouse>): List<Route> {}

        // TODO: implement once Vehicle domain class and VehicleRaw are ready
        // private fun buildVehicles(vehicleRaws: List<VehicleRaw>, warehouseMap: Map<String, Warehouse>): List<Vehicle> {}
    }
}