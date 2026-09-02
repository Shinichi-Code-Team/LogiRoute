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
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.FindStationedVehiclesByCapacityUseCase
import com.example.logiroute.domain.usecase.CalculatePricingUseCase
import com.example.logiroute.domain.usecase.FindOptimalPathUseCase
import com.example.logiroute.domain.usecase.AddVehicleToHubUseCase
import com.example.logiroute.domain.usecase.FindFewestHopsRouteUseCase
import com.example.logiroute.domain.usecase.ReroutePackageUseCase
import com.example.logiroute.domain.tree.HubTreeBuilder
import com.example.logiroute.domain.usecase.*
import com.example.logiroute.domain.model.request.DetectEmergencyCargoRescueRequest
import com.example.logiroute.domain.model.request.ExecuteEmergencyCargoPrioritizationRequest
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException

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

    val domainGraph = graphBuilder.build()

    if (!isValidDomainGraph(domainGraph)) {
        return
    }

    printDomainGraphSummary(domainGraph)

    val routers =
        createRouters(warehouseRepository)

    val findFewestHopsRouteUseCase =
        FindFewestHopsRouteUseCase(routers.bfs)

    val findOptimalPathUseCase =
        FindOptimalPathUseCase(routers.dijkstra)

    val addVehicleToHubUseCase =
        AddVehicleToHubUseCase(vehicleRepository)


    val reroutePackageUseCase = ReroutePackageUseCase(
        packageRepository = packageRepository,
        warehouseRepository = warehouseRepository
    )
    runRoutingDemo(
        domainGraph = domainGraph,
        routers = routers,
        findFewestHopsRouteUseCase = findFewestHopsRouteUseCase,
        findOptimalPathUseCase = findOptimalPathUseCase
    )
    val builder = HubTreeBuilder()

    //runBidirectionalDemo( domainGraph, routers)

    //runPricingDemo(domainGraph)
    runShipmentConsolidationDemo()
    runShipmentConsolidationOnRealData(domainGraph, routers)

    runPricingDemo(domainGraph)
    runReroutePackageDemo(domainGraph, reroutePackageUseCase)

}
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

    val warehouseRepository = CSVWarehouseRepository(loader)

    val packageRepository = CSVPackageRepository(
        loader = loader,
        warehouseRepository = warehouseRepository
    )

    val routeRepository = CSVRouteRepository(
        loader = loader,
        warehouseRepository = warehouseRepository
    )

    val vehicleRepository = CSVVehicleRepository(
        loader = loader,
        writer = fleetWriter,
        warehouseRepository = warehouseRepository
    )

    val graphBuilder = DomainGraphBuilder(
        packageRepository,
        routeRepository,
        warehouseRepository,
        vehicleRepository
    )

    val domainGraph = graphBuilder.build()

    if (domainGraph.warehouses.isEmpty() || domainGraph.packages.isEmpty()) {
        println("Domain data is not available.")
        return
    }

    val pathConstructor = PathConstructor()

    val bfsRouter = BfsRouter(
        warehouseRepository = warehouseRepository,
        pathConstructor = pathConstructor
    )

    val distanceRouter = DijkstraRouter(
        warehousesRepository = warehouseRepository,
        pathConstructor = pathConstructor,
        routeWeight = { route -> route.distanceKm }
    )

    val delayRouter = DijkstraRouter(
        warehousesRepository = warehouseRepository,
        pathConstructor = pathConstructor,
        routeWeight = { route -> route.typicalDelayMin.toDouble() }
    )

    val findFewestHopsRouteUseCase = FindFewestHopsRouteUseCase(bfsRouter)

    val findOptimalPathUseCase = FindOptimalPathUseCase(distanceRouter)

    val addVehicleToHubUseCase = AddVehicleToHubUseCase(vehicleRepository)

    val findStationedVehiclesByCapacityUseCase =
        FindStationedVehiclesByCapacityUseCase(vehicleRepository)

    val getWarehouseLoadFactorUseCase =
        GetWarehouseLoadFactorUseCase(warehouseRepository)

    val detectShipmentConsolidationUseCase =
        DetectShipmentConsolidationOpportunitiesUseCase(findOptimalPathUseCase)

    val packageSelectionSort = PackageSelectionSort()

    val prioritizeShipmentConsolidationUseCase =
        PrioritizeShipmentConsolidationUseCase(packageSelectionSort)

    val calculateVehicleUtilizationUseCase =
        CalculateVehicleUtilizationUseCase()

    val assignPackagesToBestFitVehiclesUseCase =
        AssignPackagesToBestFitVehiclesUseCase(calculateVehicleUtilizationUseCase)

    val rebalanceVehicleLoadsUseCase =
        RebalanceVehicleLoadsUseCase()

    val selectShipmentRouteUseCase = SelectShipmentRouteUseCase(
        distanceRouter = distanceRouter,
        delayRouter = delayRouter,
        bfsRouter = bfsRouter
    )

    val evaluateRouteUseCase =
        EvaluateRouteUseCase(routeRepository)

    val estimateDispatchCostUseCase =
        EstimateDispatchCostUseCase()

    val dispatchVehicleUseCase =
        DispatchVehicleUseCase()

    println()
    println("============================================")
    println("        LOGIROUTE SHIPMENT FLOW")
    println("============================================")

    println()
    println("========== STEP 1: DETECT CONSOLIDATION ==========")

    val opportunities =
        detectShipmentConsolidationUseCase(domainGraph.packages)

    println("Number of consolidation opportunities: ${opportunities.size}")

    if (opportunities.isEmpty()) {
        println("No consolidation opportunities found.")
        return
    }

    val opportunity = opportunities.first()

    println("Main Package: ${opportunity.mainPackage.id}")

    println(
        "Compatible Packages: ${
            opportunity.compatiblePackages.map { it.id }
        }"
    )

    println(
        "Shared Route: ${
            opportunity.sharedRoute.joinToString(" -> ") { it.id }
        }"
    )

    println()
    println("========== STEP 2: PRIORITIZE PACKAGES ==========")

    val prioritizedPackages =
        prioritizeShipmentConsolidationUseCase(opportunity)

    prioritizedPackages.forEachIndexed { index, packageItem ->
        println(
            "${index + 1}. ${packageItem.id} | " +
                    "Priority = ${packageItem.priority} | " +
                    "Weight = ${packageItem.weight} kg"
        )
    }

    println()
    println("========== STEP 3: SHIPMENT SERVICE ==========")

    val shipment = ShipmentGroupRequest(
        packages = prioritizedPackages,
        origin = opportunity.mainPackage.origin,
        destination = opportunity.mainPackage.destination,
        service = ShipmentService.EXPRESS
    )

    println("Shipment Service: ${shipment.service}")

    println()
    println("========== STEP 4: SELECT FINAL ROUTE ==========")

    val selectedRoute =
        selectShipmentRouteUseCase(shipment)

    println("Routing Objective: ${selectedRoute.routingObjective}")

    println(
        "Selected Route: ${
            selectedRoute.path.joinToString(" -> ") { it.id }
        }"
    )

    println()
    println("========== STEP 5: VALIDATE PACKAGES ==========")

    val finalRoutePackages = prioritizedPackages.filter { packageItem ->
        packageItem.destination in selectedRoute.path
    }

    val removedPackages = prioritizedPackages.filterNot { packageItem ->
        packageItem.destination in selectedRoute.path
    }

    println(
        "Packages compatible with final route: ${
            finalRoutePackages.map { it.id }
        }"
    )

    if (removedPackages.isNotEmpty()) {
        println(
            "Packages removed from consolidation: ${
                removedPackages.map { it.id }
            }"
        )
    }

    if (opportunity.mainPackage !in finalRoutePackages) {
        throw IllegalStateException(
            "Main package destination is not part of the selected route."
        )
    }

    println()
    println("========== STEP 6: VEHICLE UTILIZATION ==========")

    val stationedVehicles = domainGraph.vehicles.filter { vehicle ->
        vehicle.currentHub == opportunity.mainPackage.origin
    }

    if (stationedVehicles.isEmpty()) {
        println(
            "No vehicles stationed at warehouse ${opportunity.mainPackage.origin.id}"
        )
        return
    }

    stationedVehicles.forEach { vehicle ->
        val utilization =
            calculateVehicleUtilizationUseCase(vehicle)

        println()
        println("Vehicle: ${vehicle.id}")
        println("Max Capacity: ${vehicle.maxCapacityKg} kg")
        println("Current Load: ${utilization.currentLoadKg} kg")
        println("Remaining Capacity: ${utilization.remainingCapacityKg} kg")
        println("Utilization: ${utilization.utilizationPercentage}%")
        println("Cost Per Km: ${vehicle.costPerKm}")
    }

    println()
    println("========== STEP 7: BEST FIT ASSIGNMENT ==========")

    val initialAssignments = assignPackagesToBestFitVehiclesUseCase(
        packages = finalRoutePackages,
        vehicles = stationedVehicles
    )

    initialAssignments.forEach { assignment ->
        println()
        println("Vehicle: ${assignment.vehicle.id}")
        println(
            "Assigned Packages: ${
                assignment.packages.map { it.id }
            }"
        )
        println("Assigned Weight: ${assignment.totalWeightKg} kg")
        println("Remaining Capacity: ${assignment.remainingCapacityKg} kg")
    }

    println()
    println("========== STEP 8: REBALANCE VEHICLES ==========")

    val finalAssignments =
        rebalanceVehicleLoadsUseCase(initialAssignments)

    println("Vehicles before rebalancing: ${initialAssignments.size}")
    println("Vehicles after rebalancing: ${finalAssignments.size}")

    finalAssignments.forEach { assignment ->
        println()
        println("Vehicle: ${assignment.vehicle.id}")
        println(
            "Final Packages: ${
                assignment.packages.map { it.id }
            }"
        )
        println("Remaining Capacity: ${assignment.remainingCapacityKg} kg")
    }

    println()
    println("========== STEP 9: EVALUATE ROUTE ==========")

    val routeEvaluation =
        evaluateRouteUseCase(selectedRoute)

    println("Total Distance: ${routeEvaluation.totalDistanceKm} km")
    println("Expected Delay: ${routeEvaluation.totalExpectedDelayMin} min")
    println("Hop Count: ${routeEvaluation.hopCount}")

    println()
    println("========== STEP 10: DISPATCH COST ==========")

    finalAssignments.forEach { assignment ->
        val dispatchCost = estimateDispatchCostUseCase(
            vehicle = assignment.vehicle,
            routeEvaluation = routeEvaluation
        )

        println()
        println("Vehicle: ${assignment.vehicle.id}")
        println("Cost Per Km: ${assignment.vehicle.costPerKm}")
        println("Route Distance: ${routeEvaluation.totalDistanceKm}")
        println("Estimated Dispatch Cost: $dispatchCost")
    }

    println()
    println("========== STEP 11: DISPATCH ==========")

    finalAssignments.forEach { assignment ->
        println()
        println("Dispatching vehicle: ${assignment.vehicle.id}")

        println(
            "Packages before dispatch: ${
                assignment.packages.map { it.id }
            }"
        )

        val dispatchedPackages = dispatchVehicleUseCase(
            warehouse = opportunity.mainPackage.origin,
            assignment = assignment
        )

        println(
            "Dispatched Packages: ${
                dispatchedPackages.map { it.id }
            }"
        )
    }

    println()
    println("============================================")
    println("              FLOW COMPLETED")
    println("============================================")

    println(
        "Warehouse remaining packages: ${
            opportunity.mainPackage.origin.cargoQueue.map { it.id }
        }"
    )

    finalAssignments.forEach { assignment ->
        println(
            "${assignment.vehicle.id} loaded packages: ${
                assignment.vehicle.loadedPackages.map { it.id }
            }"
        )
    }

    val detectRescueUseCase =
        DetectEmergencyCargoRescueOpportunitiesUseCase(
            packageRepository = packageRepository,
            vehicleRepository = vehicleRepository,
            warehouseRepository = warehouseRepository,
            findOptimalPathUseCase = findOptimalPathUseCase
        )

    val executePrioritizationUseCase =
        ExecuteEmergencyCargoPrioritizationUseCase(
            packageRepository = packageRepository
        )

    println()
    println("========== EMERGENCY CARGO RESCUE ==========")

    val sampleWarehouseId = domainGraph.warehouses.first().id

    try {
        val detectRequest = DetectEmergencyCargoRescueRequest(warehouseId = sampleWarehouseId)
        val rescueOpportunities = detectRescueUseCase(detectRequest)

        rescueOpportunities.forEach { rescueOpportunity ->
            val executeRequest = ExecuteEmergencyCargoPrioritizationRequest(rescueOpportunity)
            val dispatchPlan = executePrioritizationUseCase(executeRequest)

            println()
            println("Current Warehouse : ${rescueOpportunity.currentWarehouse.name}")
            println("Next Hop Transit  : ${rescueOpportunity.nextHopWarehouse.name}")
            println("Assigned Vehicle  : ${dispatchPlan.vehicle.id}")
            println("Loaded Urgent     : ${dispatchPlan.loadedUrgentPackages.map { it.id }}")
            println("Offloaded Low Prio: ${dispatchPlan.offloadedLowPriorityPackages.map { it.id }}")
            println("Total Weight      : ${dispatchPlan.totalWeight} kg")
            println("Remaining Capacity: ${dispatchPlan.remainingCapacity} kg")
        }
    } catch (e: LogisticsException) {
        println("Emergency Process Notice: ${e.message}")
    } catch (e: Exception) {
        println("Unexpected Error: ${e.message}")
    }
}

private fun runReroutePackageDemo(
    domainGraph: DomainGraph,
    reroutePackageUseCase: ReroutePackageUseCase
) {
    val firstPackage = domainGraph.packages.firstOrNull()
    val secondWarehouse = domainGraph.warehouses.getOrNull(1)

    if (firstPackage != null && secondWarehouse != null) {
        println("\n========== REROUTE PACKAGE DEMO ==========")
        println(" Original Package: ${firstPackage.id}")
        println("Original Destination: ${firstPackage.destination.name}")
        println(" New Destination: ${secondWarehouse.name}")

        val reroutedPackage = reroutePackageUseCase(
            packageId = firstPackage.id,
            newDestinationId = secondWarehouse.id
        )

        println(" Package rerouted successfully!")
        println(" New Destination: ${reroutedPackage.destination.name}")
        println(" Package: ${reroutedPackage.id} (${reroutedPackage.priority}) - ${reroutedPackage.weight}kg")
    } else {
        println(" Not enough data to demonstrate rerouting!")
    }
}
