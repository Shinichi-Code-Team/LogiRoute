package com.example.logiroute

import com.example.logiroute.com.example.logiroute.domain.model.request.GetWarehouseLoadFactorRequest
import com.example.logiroute.com.example.logiroute.domain.usecase.model.request.FindStationedVehiclesRequest
import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.data.processing.writer.FleetWriter
import com.example.logiroute.data.repository.*
import com.example.logiroute.domain.builder.DomainGraphBuilder
import com.example.logiroute.domain.command.AssignPackageToQueueCommand
import com.example.logiroute.domain.command.CommandInvoker
import com.example.logiroute.domain.logic.algorithm.routing.*
import com.example.logiroute.domain.logic.algorithm.sorting.PackageSelectionSort
import com.example.logiroute.domain.logic.packagepricing.basepricing.EcoStrategy
import com.example.logiroute.domain.logic.packagepricing.basepricing.RoutePricingEngine
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.request.*
import com.example.logiroute.domain.usecase.*

fun main() {
    val loader = Loader()
    val warehouseRepository = CSVWarehouseRepository(loader)
    val packageRepository = CSVPackageRepository(loader, warehouseRepository)
    val routeRepository = CSVRouteRepository(loader, warehouseRepository)
    val vehicleRepository = CSVVehicleRepository(loader, FleetWriter("fleet.csv"), warehouseRepository)

    // قراءة البيانات وتغليفها بـ try-catch لمنع الانهيار بسبب أي بيانات تالفة في CSV
    val graph = try {
        DomainGraphBuilder(
            packageRepository,
            routeRepository,
            warehouseRepository,
            vehicleRepository
        ).build()
    } catch (e: Exception) {
        println("[Warning] Failed to build domain graph from CSV: ${e.message}")
        return
    }

    if (graph.packages.isEmpty() || graph.warehouses.isEmpty()) {
        println("[Warning] Graph is empty. Check CSV resources.")
        return
    }

    val pathConstructor = PathConstructor()
    val bfsRouter = BfsRouter(warehouseRepository, pathConstructor)
    val distanceRouter = DijkstraRouter(warehouseRepository, pathConstructor) { it.distanceKm }
    val delayRouter = DijkstraRouter(warehouseRepository, pathConstructor) { it.typicalDelayMin.toDouble() }

    val findOptimalPath = FindOptimalPathUseCase(distanceRouter)
    val findFewestHops = FindFewestHopsRouteUseCase(bfsRouter)
    val calculateUtilization = CalculateVehicleUtilizationUseCase()
    val detectConsolidation = DetectShipmentConsolidationOpportunitiesUseCase(findOptimalPath)
    val prioritizeConsolidation = PrioritizeShipmentConsolidationUseCase(PackageSelectionSort())
    val selectRoute = SelectShipmentRouteUseCase(distanceRouter, delayRouter, bfsRouter)
    val findVehicles = FindStationedVehiclesByCapacityUseCase(vehicleRepository)
    val assignVehicles = AssignPackagesToBestFitVehiclesUseCase(calculateUtilization)
    val rebalanceLoads = RebalanceVehicleLoadsUseCase()
    val evaluateRoute = EvaluateRouteUseCase(routeRepository)
    val estimateCost = EstimateDispatchCostUseCase()
    val dispatchVehicle = DispatchVehicleUseCase()

    val opportunities = detectConsolidation(graph.packages)
    val opportunity = opportunities.firstOrNull() ?: return
    val packages = prioritizeConsolidation(opportunity)

    val shipment = ShipmentGroupRequest(
        packages = packages,
        origin = opportunity.mainPackage.origin,
        destination = opportunity.mainPackage.destination,
        service = ShipmentService.EXPRESS
    )

    val selectedRoute = selectRoute(shipment)
    val routePackages = packages.filter { it.destination in selectedRoute.path }
    val minCapacity = routePackages.maxOfOrNull { it.weight } ?: return

    val vehicles = findVehicles(
        FindStationedVehiclesRequest(shipment.origin.id, minCapacity)
    )

    val assignments = assignVehicles(routePackages, vehicles)
    val finalAssignments = rebalanceLoads(assignments)
    val routeEvaluation = evaluateRoute(selectedRoute)

    println("\nShipment")
    println("Packages: ${routePackages.map { it.id }}")
    println("Route: ${selectedRoute.path.joinToString(" -> ") { it.id }}")
    println("Objective: ${selectedRoute.routingObjective}")

    finalAssignments.forEach {
        val cost = estimateCost(it.vehicle, routeEvaluation)
        val loaded = dispatchVehicle(shipment.origin, it)
        println("${it.vehicle.id}: ${loaded.map { pkg -> pkg.id }} | Cost: $cost")
    }

    val source = graph.warehouses.first()
    val destination = graph.warehouses.firstOrNull { it != source } ?: return

    val shortestPath = findOptimalPath(source, destination)
    val fewestHopsPath = findFewestHops(source, destination)

    println("\nRouting")
    println("Optimal: ${shortestPath.joinToString(" -> ") { it.id }}")
    println("Fewest hops: ${fewestHopsPath.joinToString(" -> ") { it.id }}")

    graph.vehicles.firstOrNull()?.let { vehicle ->
        val utilization = calculateUtilization(vehicle)
        println("\nUtilization: ${vehicle.id} = ${utilization.utilizationPercentage}%")
    }

    val pricing = CalculatePricingUseCase(RoutePricingEngine(EcoStrategy()))
    graph.packages.firstOrNull()?.let {
        val price = pricing(it, routeEvaluation.totalDistanceKm)
        println("Package price: $price")
    }

    val loadFactorUseCase = GetWarehouseLoadFactorUseCase(warehouseRepository)
    graph.warehouses.firstOrNull { it.stationedVehicles.isNotEmpty() }?.let {
        try {
            val factor = loadFactorUseCase(GetWarehouseLoadFactorRequest(it.id))
            println("Warehouse load factor: $factor")
        } catch (e: Exception) {
            println("Could not calculate load factor: ${e.message}")
        }
    }

    val findBackhaul = FindBackhaulCandidatesUseCase(packageRepository)
    val optimizeBackhaul = OptimizeBackhaulUseCase()

    graph.vehicles.firstOrNull()?.let { vehicle ->
        val returnPath = listOf(vehicle.currentHub)
        val candidates = findBackhaul(vehicle, vehicle.currentHub, returnPath)
        val plan = optimizeBackhaul(vehicle, candidates, returnPath)

        println("\nBackhaul")
        println("Candidates: ${candidates.size}")
        println("Selected: ${plan.selectedPackages.map { it.id }}")
    }

    val analyzeTree = AnalyzeTreePerformanceUseCase()
    val treeReport = analyzeTree()

    println("\nTree")
    println("Unbalanced height: ${treeReport.unbalancedHeight}")
    println("Balanced height: ${treeReport.balancedHeight}")

    val traceLineage = TraceHubLineageUseCase()
    val sampleHub = com.example.logiroute.com.example.logiroute.domain.model.request.HubNode(
        warehouse = source,
        hubType = com.example.logiroute.com.example.logiroute.domain.model.request.HubType.GLOBAL_HUB
    )

    val lineage = traceLineage(sampleHub)
    println("Lineage: ${lineage.map { it.warehouse.id }}")

    val detectEmergency = DetectEmergencyCargoRescueOpportunitiesUseCase(
        packageRepository,
        vehicleRepository,
        warehouseRepository,
        findOptimalPath
    )

    val executeEmergency = ExecuteEmergencyCargoPrioritizationUseCase(packageRepository)

    graph.packages.firstOrNull { it.priority == Priority.URGENT }?.let { urgent ->
        try {
            val rescue = detectEmergency(DetectEmergencyCargoRescueRequest(urgent.origin.id))
            rescue.firstOrNull()?.let {
                val plan = executeEmergency(ExecuteEmergencyCargoPrioritizationRequest(it))
                println("\nEmergency: ${plan.loadedUrgentPackages.map { pkg -> pkg.id }}")
            }
        } catch (e: Exception) {
            println("Emergency rescue check skipped: ${e.message}")
        }
    }

    val assignToQueue = AssignPackageToCargoQueueUseCase()
    val samplePackage = graph.packages.first()

    val addedToQueue = assignToQueue(samplePackage.origin, samplePackage)
    println("\nQueue assignment: $addedToQueue")

    val addVehicle = AddVehicleToHubUseCase(vehicleRepository)

    graph.vehicles.firstOrNull()?.let {
        println("Vehicle added: ${addVehicle(it)}")
    }

    val reroutePackage = ReroutePackageUseCase(packageRepository, warehouseRepository)

    graph.packages.firstOrNull()?.let { pkg ->
        graph.warehouses.firstOrNull { it != pkg.destination }?.let { newDestination ->
            try {
                val rerouted = reroutePackage(pkg.id, newDestination.id)
                println("Rerouted: ${rerouted.id} -> ${rerouted.destination.id}")
            } catch (e: Exception) {
                println("Reroute skipped: ${e.message}")
            }
        }
    }
    println("\n=== Telemetry & Undo/Redo Testing (Real Domain Data) ===")

    val invoker = CommandInvoker()
    val testWarehouse = graph.warehouses.firstOrNull()

    if (testWarehouse != null) {
        val sampleTemplate = graph.packages.firstOrNull()

        if (sampleTemplate != null) {
            val pkgA = sampleTemplate.copy(id = "PKG-TEST-001", origin = testWarehouse)
            val pkgB = sampleTemplate.copy(id = "PKG-TEST-002", origin = testWarehouse)
            val pkgC = sampleTemplate.copy(id = "PKG-TEST-003", origin = testWarehouse)

            println("\n--- 1. Testing Empty Stacks ---")
            println("[Telemetry] Undo on empty stack -> Result: ${invoker.undoLast()}")
            println("[Telemetry] Redo on empty stack -> Result: ${invoker.redoLast()}")

            println("\n--- 2. Testing Command Execution ---")
            println("[Telemetry] Initial Queue Size: ${testWarehouse.cargoQueue.size}")

            val cmd1 = AssignPackageToQueueCommand(
                assignPackageToCargoQueueUseCase = assignToQueue,
                warehouse = testWarehouse,
                packageItem = pkgA
            )
            val cmd2 = AssignPackageToQueueCommand(
                assignPackageToCargoQueueUseCase = assignToQueue,
                warehouse = testWarehouse,
                packageItem = pkgB
            )

            try {
                invoker.executeCommand(cmd1)
                println("[Execute Log] Cmd1 Executed (${pkgA.id}) -> Success | Queue Size: ${testWarehouse.cargoQueue.size} | History Size: ${invoker.historySize()}")
            } catch (e: Exception) {
                println("[Execute Log] Cmd1 (${pkgA.id}) Failed: ${e.message} | Queue Size: ${testWarehouse.cargoQueue.size}")
            }

            try {
                invoker.executeCommand(cmd2)
                println("[Execute Log] Cmd2 Executed (${pkgB.id}) -> Success | Queue Size: ${testWarehouse.cargoQueue.size} | History Size: ${invoker.historySize()}")
            } catch (e: Exception) {
                println("[Execute Log] Cmd2 (${pkgB.id}) Failed: ${e.message} | Queue Size: ${testWarehouse.cargoQueue.size}")
            }

            println("\n--- 3. Testing Multiple Undo Operations ---")
            val undo1 = invoker.undoLast()
            println("[Undo Log] 1st Undo Executed -> Result: $undo1 | Queue Size: ${testWarehouse.cargoQueue.size} | History Size: ${invoker.historySize()}")

            val undo2 = invoker.undoLast()
            println("[Undo Log] 2nd Undo Executed -> Result: $undo2 | Queue Size: ${testWarehouse.cargoQueue.size} | History Size: ${invoker.historySize()}")

            println("\n--- 4. Testing Multiple Redo Operations ---")
            val redo1 = invoker.redoLast()
            println("[Redo Log] 1st Redo Executed -> Result: $redo1 | Queue Size: ${testWarehouse.cargoQueue.size} | History Size: ${invoker.historySize()}")

            val redo2 = invoker.redoLast()
            println("[Redo Log] 2nd Redo Executed -> Result: $redo2 | Queue Size: ${testWarehouse.cargoQueue.size} | History Size: ${invoker.historySize()}")

            println("\n--- 5. Testing Redo History Clearance ---")
            invoker.undoLast()
            println("[Telemetry] Undo executed. History Size before new command: ${invoker.historySize()}")

            val cmd3 = AssignPackageToQueueCommand(
                assignPackageToCargoQueueUseCase = assignToQueue,
                warehouse = testWarehouse,
                packageItem = pkgC
            )

            try {
                invoker.executeCommand(cmd3)
                println("[Execute Log] Cmd3 Executed (${pkgC.id}) -> Success | Queue Size: ${testWarehouse.cargoQueue.size}")
            } catch (e: Exception) {
                println("[Execute Log] Cmd3 Failed: ${e.message}")
            }

            val redoResult = invoker.redoLast()
            println("[Telemetry] Attempting Redo after new command execution (Expected: false) -> Result: $redoResult")

            println("\n--- Final Domain Verification ---")
            println("Final Cargo Queue Size: ${testWarehouse.cargoQueue.size}")
            println("Final History Size: ${invoker.historySize()}")
        }
    } else {
        println("[Warning] No warehouses loaded to run telemetry flow.")
    }

    println("\nDone")
}