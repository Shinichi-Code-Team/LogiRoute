package com.example.logiroute.domain.builder

import com.example.logiroute.data.dataholder.*
import com.example.logiroute.domain.model.*

class DomainGraphBuilder {
//    fun build(input: DomainGraphInput): DomainGraph {
//        val warehouses = buildWarehouses()
//        val warehouseMap = buildWarehouseIndex()
//        val packages = buildPackages(input.packageRaws, warehouseMap)
//        val routes = buildRoutes()
//        val vehicles = buildVehicles()
//        return DomainGraph(warehouses, packages, routes, vehicles)
//    }
//
//    private fun buildWarehouses(warehouseRaw: List<WarehouseRaw>): List<Warehouse> {}
//    private fun buildWarehouseIndex(warehouse: List<Warehouse>): Map<String, Warehouse> {}
//    private fun buildPackages(
//        packageRaws: List<PackageRaw>,
//        warehouseMap: Map<String, Warehouse>
//    ): MutableList<Package> {
//        val packages = mutableListOf<Package>()
//        for (packageRaw in packageRaws) {
//            packages.add(buildPackage(packageRaw, warehouseMap))
//        }
//        return packages
//    }
//
//    private fun buildPackage(packageRaw: PackageRaw, warehouseMap: Map<String, Warehouse>): Package {
//
//        val origin = warehouseMap.getValue(packageRaw.originHubId)
//        val destination = warehouseMap.getValue(packageRaw.destinationHubId)
//        val packageDomain = Package(packageRaw.id, packageRaw.weight, origin, destination, packageRaw.priority)
//        origin.addPackage(packageDomain)
//        return packageDomain
//    }
//
//
//    private fun buildRoutes() {}
//    private fun buildVehicles() {}
}