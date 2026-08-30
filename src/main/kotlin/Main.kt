package com.example.logiroute

import com.example.logiroute.com.example.logiroute.domain.model.request.HubHierarchyRaw
import com.example.logiroute.com.example.logiroute.domain.model.request.HubType
import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.data.processing.writer.FleetWriter
import com.example.logiroute.data.repository.CSVPackageRepository
import com.example.logiroute.data.repository.CSVRouteRepository
import com.example.logiroute.data.repository.CSVVehicleRepository
import com.example.logiroute.data.repository.CSVWarehouseRepository
import com.example.logiroute.domain.builder.DomainGraphBuilder
import com.example.logiroute.domain.logic.algorithm.routing.BfsRouter
import com.example.logiroute.domain.logic.algorithm.routing.DijkstraRouter
import com.example.logiroute.domain.logic.algorithm.routing.PathConstructor
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.tree.HubTreeBuilder
import com.example.logiroute.domain.usecase.*

fun main() {
    val globalWarehouse = Warehouse(
        id = "G01",
        name = "Global Hub",
        regionalZone = "GLOBAL",
        latitude = 0.0,
        longitude = 0.0
    )

    val regionalWarehouse = Warehouse(
        id = "R01",
        name = "Regional Center",
        regionalZone = "NORTH",
        latitude = 1.0,
        longitude = 1.0
    )

    val localWarehouse = Warehouse(
        id = "L01",
        name = "Local Depot",
        regionalZone = "NORTH",
        latitude = 2.0,
        longitude = 2.0
    )
    val hierarchy = listOf(
        HubHierarchyRaw(
            warehouseId = "G01",
            hubType = HubType.GLOBAL_HUB,
            parentWarehouseId = null
        ),

        HubHierarchyRaw(
            warehouseId = "R01",
            hubType = HubType.REGIONAL_CENTER,
            parentWarehouseId = "G01"
        ),

        HubHierarchyRaw(
            warehouseId = "L01",
            hubType = HubType.LOCAL_DEPOT,
            parentWarehouseId = "R01"
        )
    )
    val builder = HubTreeBuilder()

    val root = builder.buildTree(
        warehouses = listOf(
            globalWarehouse,
            regionalWarehouse,
            localWarehouse
        ),
        hierarchy = hierarchy
    )
    val localNode = root.children
        .first()
        .children
        .first()

    val traceHubLineageUseCase = TraceHubLineageUseCase()

    val lineage = traceHubLineageUseCase(localNode)

    lineage.forEach {
        println("${it.hubType}: ${it.warehouse.name}")
    }


   val loader = Loader()
    val fleetWriter = FleetWriter("fleet.csv")

    val warehouseRepository =
        CSVWarehouseRepository(loader)

    val packageRepository =
        CSVPackageRepository(
            loader = loader,
            warehouseRepository = warehouseRepository
        )

    val routeRepository =
        CSVRouteRepository(
            loader = loader,
            warehouseRepository = warehouseRepository
        )

    val vehicleRepository =
        CSVVehicleRepository(
            loader = loader,
            writer = fleetWriter,
            warehouseRepository = warehouseRepository
        )

    val graphBuilder =
        DomainGraphBuilder(
            packageRepository,
            routeRepository,
            warehouseRepository,
            vehicleRepository
        )

    val domainGraph = graphBuilder.build()

    if (
        domainGraph.warehouses.isEmpty() ||
        domainGraph.packages.isEmpty()
    ) {
        println("Domain data is not available.")
        return
    }

    val pathConstructor = PathConstructor()

    val bfsRouter =
        BfsRouter(
            warehouseRepository = warehouseRepository,
            pathConstructor = pathConstructor
        )

    val dijkstraRouter =
        DijkstraRouter(
            warehousesRepository = warehouseRepository,
            pathConstructor = pathConstructor
        )


    val findFewestHopsRouteUseCase =
        FindFewestHopsRouteUseCase(
            bfsRouter
        )

    val findOptimalPathUseCase =
        FindOptimalPathUseCase(
            dijkstraRouter
        )

    val addVehicleToHubUseCase =
        AddVehicleToHubUseCase(
            vehicleRepository
        )

    val findStationedVehiclesByCapacityUseCase =
        FindStationedVehiclesByCapacityUseCase(
            vehicleRepository
        )

    val getWarehouseLoadFactorUseCase =
        GetWarehouseLoadFactorUseCase(
            warehouseRepository
        )

    val detectShipmentConsolidationUseCase =
        DetectShipmentConsolidationOpportunitiesUseCase(
            findOptimalPathUseCase
        )

    val prioritizeShipmentConsolidationUseCase =
        PrioritizeShipmentConsolidationUseCase()

    val dispatchVehicleUseCase =
        DispatchVehicleUseCase()

    val packageItem =
        domainGraph.packages.first()

    val optimalPath =
        findOptimalPathUseCase(
            packageItem.origin,
            packageItem.destination
        )

    println("========== OPTIMAL PATH ==========")

    println(
        optimalPath.joinToString(" -> ") {
            it.name
        }
    )


    val opportunities =
        detectShipmentConsolidationUseCase(
            domainGraph.packages
        )

    println()
    println("========== SHIPMENT CONSOLIDATION ==========")

    opportunities.forEach { opportunity ->

        val vehicle =
            domainGraph.vehicles.firstOrNull {
                it.currentHub ==
                        opportunity.mainPackage.origin
            }

        if (vehicle != null) {

            val plan =
                prioritizeShipmentConsolidationUseCase(
                    opportunity = opportunity,
                    vehicle = vehicle
                )

            println()
            println(
                "Main Package: ${opportunity.mainPackage.id}"
            )

            println(
                "Compatible Packages: ${
                    opportunity.compatiblePackages
                        .map { it.id }
                }"
            )

            println(
                "Shared Route: ${
                    opportunity.sharedRoute
                        .map { it.id }
                }"
            )

            println(
                "Vehicle: ${vehicle.id}"
            )

            println(
                "Selected Packages: ${
                    plan.selectedPackages
                        .map { it.id }
                }"
            )

            println(
                "Total Weight: ${plan.totalWeight} kg"
            )

            println(
                "Remaining Capacity: ${
                    plan.remainingCapacity
                } kg"
            )
        }
    }
}
