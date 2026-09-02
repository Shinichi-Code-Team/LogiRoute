package com.example.logiroute

import com.example.logiroute.com.example.logiroute.domain.model.request.HubHierarchyRaw
import com.example.logiroute.com.example.logiroute.domain.model.request.HubType
import com.example.logiroute.com.example.logiroute.domain.usecase.ValidatePackagesAgainstFinalRouteUseCase
import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.data.processing.writer.FleetWriter
import com.example.logiroute.data.repository.*
import com.example.logiroute.domain.builder.*
import com.example.logiroute.domain.logic.algorithm.routing.*
import com.example.logiroute.domain.logic.algorithm.sorting.PackageSelectionSort
import com.example.logiroute.domain.logic.algorithm.tree.HubTreeBuilder
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.*
import com.example.logiroute.domain.usecase.*
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException

fun main() {
    val loader = Loader()
    val fleetWriter = FleetWriter("fleet.csv")
    val warehouseRepository = CSVWarehouseRepository(loader)
    val packageRepository = CSVPackageRepository(loader, warehouseRepository)
    val routeRepository = CSVRouteRepository(loader, warehouseRepository)
    val vehicleRepository = CSVVehicleRepository(loader, fleetWriter, warehouseRepository)
    val domainGraph = DomainGraphBuilder(packageRepository, routeRepository, warehouseRepository, vehicleRepository).build()

    if (domainGraph.warehouses.isEmpty() || domainGraph.packages.isEmpty()) {
        println("Domain data is not available.")
        return
    }

    val pathConstructor = PathConstructor()
    val bfsRouter = BfsRouter(warehouseRepository, pathConstructor)
    val distanceRouter = DijkstraRouter(warehouseRepository, pathConstructor) { it.distanceKm }
    val delayRouter = DijkstraRouter(warehouseRepository, pathConstructor) { it.typicalDelayMin.toDouble() }
    val findOptimalPathUseCase = FindOptimalPathUseCase(distanceRouter)
    val calculateVehicleUtilizationUseCase = CalculateVehicleUtilizationUseCase()

    runHubHierarchyDemo()
    runShipmentFlow(
        domainGraph = domainGraph,
        detectConsolidation = DetectShipmentConsolidationOpportunitiesUseCase(findOptimalPathUseCase),
        prioritizeConsolidation = PrioritizeShipmentConsolidationUseCase(PackageSelectionSort()),
        validateFinalRoute = ValidatePackagesAgainstFinalRouteUseCase(),
        calculateUtilization = calculateVehicleUtilizationUseCase,
        assignBestFit = AssignPackagesToBestFitVehiclesUseCase(calculateVehicleUtilizationUseCase),
        rebalanceLoads = RebalanceVehicleLoadsUseCase(),
        selectRoute = SelectShipmentRouteUseCase(distanceRouter, delayRouter, bfsRouter),
        evaluateRoute = EvaluateRouteUseCase(routeRepository),
        estimateCost = EstimateDispatchCostUseCase(),
        dispatchVehicle = DispatchVehicleUseCase()
    )
    runEmergencyCargoRescue(
        domainGraph = domainGraph,
        packageRepository = packageRepository,
        vehicleRepository = vehicleRepository,
        warehouseRepository = warehouseRepository,
        findOptimalPathUseCase = findOptimalPathUseCase
    )
    runReroutePackageDemo(domainGraph, ReroutePackageUseCase(packageRepository, warehouseRepository))
}

private fun runHubHierarchyDemo() {
    val globalWarehouse = Warehouse("G01", "Global Hub", "GLOBAL", 0.0, 0.0)
    val regionalWarehouse = Warehouse("R01", "Regional Center", "NORTH", 1.0, 1.0)
    val localWarehouse = Warehouse("L01", "Local Depot", "NORTH", 2.0, 2.0)
    val hierarchy = listOf(
        HubHierarchyRaw("G01", HubType.GLOBAL_HUB, null),
        HubHierarchyRaw("R01", HubType.REGIONAL_CENTER, "G01"),
        HubHierarchyRaw("L01", HubType.LOCAL_DEPOT, "R01")
    )
    val root = HubTreeBuilder().buildTree(
        warehouses = listOf(globalWarehouse, regionalWarehouse, localWarehouse),
        hierarchy = hierarchy
    )
    val localNode = root.children.first().children.first()
    TraceHubLineageUseCase()(localNode).forEach { println("${it.hubType}: ${it.warehouse.name}") }
}

private fun runShipmentFlow(
    domainGraph: DomainGraph,
    detectConsolidation: DetectShipmentConsolidationOpportunitiesUseCase,
    prioritizeConsolidation: PrioritizeShipmentConsolidationUseCase,
    validateFinalRoute: ValidatePackagesAgainstFinalRouteUseCase,
    calculateUtilization: CalculateVehicleUtilizationUseCase,
    assignBestFit: AssignPackagesToBestFitVehiclesUseCase,
    rebalanceLoads: RebalanceVehicleLoadsUseCase,
    selectRoute: SelectShipmentRouteUseCase,
    evaluateRoute: EvaluateRouteUseCase,
    estimateCost: EstimateDispatchCostUseCase,
    dispatchVehicle: DispatchVehicleUseCase
) {
    printHeader("LOGIROUTE SHIPMENT FLOW")
    printStep(1, "DETECT CONSOLIDATION")
    val opportunities = detectConsolidation(domainGraph.packages)
    println("Number of consolidation opportunities: ${opportunities.size}")
    if (opportunities.isEmpty()) {
        println("No consolidation opportunities found.")
        return
    }

    val opportunity = opportunities.first()
    println("Main Package: ${opportunity.mainPackage.id}")
    println("Compatible Packages: ${opportunity.compatiblePackages.map { it.id }}")
    println("Shared Route: ${opportunity.sharedRoute.joinToString(" -> ") { it.id }}")

    printStep(2, "PRIORITIZE PACKAGES")
    val prioritizedPackages = prioritizeConsolidation(opportunity)
    prioritizedPackages.forEachIndexed { index, packageItem ->
        println("${index + 1}. ${packageItem.id} | Priority = ${packageItem.priority} | Weight = ${packageItem.weight} kg")
    }

    printStep(3, "SHIPMENT SERVICE")
    val shipment = ShipmentGroupRequest(
        packages = prioritizedPackages,
        origin = opportunity.mainPackage.origin,
        destination = opportunity.mainPackage.destination,
        service = ShipmentService.EXPRESS
    )
    println("Shipment Service: ${shipment.service}")

    printStep(4, "SELECT FINAL ROUTE")
    val selectedRoute = selectRoute(shipment)
    println("Routing Objective: ${selectedRoute.routingObjective}")
    println("Selected Route: ${selectedRoute.path.joinToString(" -> ") { it.id }}")

    printStep(5, "VALIDATE PACKAGES")
    val validation = validateFinalRoute(
        packages = prioritizedPackages,
        finalRoutePath = selectedRoute.path,
        mainPackage = opportunity.mainPackage
    )
    println("Packages compatible with final route: ${validation.validPackages.map { it.id }}")
    if (validation.removedPackages.isNotEmpty()) {
        println("Packages kept at warehouse: ${validation.removedPackages.map { it.id }}")
    }

    printStep(6, "VEHICLE UTILIZATION")
    val stationedVehicles = domainGraph.vehicles.filter { it.currentHub == opportunity.mainPackage.origin }
    if (stationedVehicles.isEmpty()) {
        println("No vehicles stationed at warehouse ${opportunity.mainPackage.origin.id}")
        return
    }
    stationedVehicles.forEach { vehicle ->
        val utilization = calculateUtilization(vehicle)
        println("\nVehicle: ${vehicle.id}")
        println("Max Capacity: ${vehicle.maxCapacityKg} kg")
        println("Current Load: ${utilization.currentLoadKg} kg")
        println("Remaining Capacity: ${utilization.remainingCapacityKg} kg")
        println("Utilization: ${utilization.utilizationPercentage}%")
        println("Cost Per Km: ${vehicle.costPerKm}")
    }

    printStep(7, "BEST FIT ASSIGNMENT")
    val initialAssignments = assignBestFit(validation.validPackages, stationedVehicles)
    initialAssignments.forEach { assignment ->
        println("\nVehicle: ${assignment.vehicle.id}")
        println("Assigned Packages: ${assignment.packages.map { it.id }}")
        println("Assigned Weight: ${assignment.totalWeightKg} kg")
        println("Remaining Capacity: ${assignment.remainingCapacityKg} kg")
    }

    printStep(8, "REBALANCE VEHICLES")
    val finalAssignments = rebalanceLoads(initialAssignments)
    println("Vehicles before rebalancing: ${initialAssignments.size}")
    println("Vehicles after rebalancing: ${finalAssignments.size}")
    finalAssignments.forEach { assignment ->
        println("\nVehicle: ${assignment.vehicle.id}")
        println("Final Packages: ${assignment.packages.map { it.id }}")
        println("Remaining Capacity: ${assignment.remainingCapacityKg} kg")
    }

    printStep(9, "EVALUATE ROUTE")
    val routeEvaluation = evaluateRoute(selectedRoute)
    println("Total Distance: ${routeEvaluation.totalDistanceKm} km")
    println("Expected Delay: ${routeEvaluation.totalExpectedDelayMin} min")
    println("Hop Count: ${routeEvaluation.hopCount}")

    printStep(10, "DISPATCH COST")
    finalAssignments.forEach { assignment ->
        val dispatchCost = estimateCost(assignment.vehicle, routeEvaluation)
        println("\nVehicle: ${assignment.vehicle.id}")
        println("Cost Per Km: ${assignment.vehicle.costPerKm}")
        println("Route Distance: ${routeEvaluation.totalDistanceKm}")
        println("Estimated Dispatch Cost: $dispatchCost")
    }

    printStep(11, "DISPATCH")
    finalAssignments.forEach { assignment ->
        println("\nDispatching vehicle: ${assignment.vehicle.id}")
        println("Packages before dispatch: ${assignment.packages.map { it.id }}")
        val dispatchedPackages = dispatchVehicle(
            warehouse = opportunity.mainPackage.origin,
            assignment = assignment
        )
        println("Dispatched Packages: ${dispatchedPackages.map { it.id }}")
    }

    printHeader("FLOW COMPLETED")
    println("Warehouse remaining packages: ${opportunity.mainPackage.origin.cargoQueue.map { it.id }}")
    finalAssignments.forEach { assignment ->
        println("${assignment.vehicle.id} loaded packages: ${assignment.vehicle.loadedPackages.map { it.id }}")
    }
}

private fun runEmergencyCargoRescue(
    domainGraph: DomainGraph,
    packageRepository: CSVPackageRepository,
    vehicleRepository: CSVVehicleRepository,
    warehouseRepository: CSVWarehouseRepository,
    findOptimalPathUseCase: FindOptimalPathUseCase
) {
    val detectRescue = DetectEmergencyCargoRescueOpportunitiesUseCase(
        packageRepository,
        vehicleRepository,
        warehouseRepository,
        findOptimalPathUseCase
    )
    val executePrioritization = ExecuteEmergencyCargoPrioritizationUseCase(packageRepository)
    println("\n========== EMERGENCY CARGO RESCUE ==========")

    try {
        val request = DetectEmergencyCargoRescueRequest(domainGraph.warehouses.first().id)
        detectRescue(request).forEach { opportunity ->
            val plan = executePrioritization(ExecuteEmergencyCargoPrioritizationRequest(opportunity))
            println("\nCurrent Warehouse : ${opportunity.currentWarehouse.name}")
            println("Next Hop Transit  : ${opportunity.nextHopWarehouse.name}")
            println("Assigned Vehicle  : ${plan.vehicle.id}")
            println("Loaded Urgent     : ${plan.loadedUrgentPackages.map { it.id }}")
            println("Offloaded Low Prio: ${plan.offloadedLowPriorityPackages.map { it.id }}")
            println("Total Weight      : ${plan.totalWeight} kg")
            println("Remaining Capacity: ${plan.remainingCapacity} kg")
        }
    } catch (e: LogisticsException) {
        println("Emergency Process Notice: ${e.message}")
    } catch (e: Exception) {
        println("Unexpected Error: ${e.message}")
    }
}

private fun runReroutePackageDemo(domainGraph: DomainGraph, reroutePackageUseCase: ReroutePackageUseCase) {
    val firstPackage = domainGraph.packages.firstOrNull()
    val secondWarehouse = domainGraph.warehouses.getOrNull(1)
    if (firstPackage == null || secondWarehouse == null) {
        println("Not enough data to demonstrate rerouting!")
        return
    }

    println("\n========== REROUTE PACKAGE DEMO ==========")
    println("Original Package: ${firstPackage.id}")
    println("Original Destination: ${firstPackage.destination.name}")
    println("New Destination: ${secondWarehouse.name}")
    val reroutedPackage = reroutePackageUseCase(firstPackage.id, secondWarehouse.id)
    println("Package rerouted successfully!")
    println("New Destination: ${reroutedPackage.destination.name}")
    println("Package: ${reroutedPackage.id} (${reroutedPackage.priority}) - ${reroutedPackage.weight} kg")
}

private fun printStep(number: Int, title: String) {
    println("\n========== STEP $number: $title ==========")
}

private fun printHeader(title: String) {
    println("\n============================================")
    println("              $title")
    println("============================================")
}
