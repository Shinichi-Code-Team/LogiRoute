package com.example.logiroute

import com.example.logiroute.com.example.logiroute.domain.model.request.GetWarehouseLoadFactorRequest
import com.example.logiroute.com.example.logiroute.domain.usecase.model.request.FindStationedVehiclesRequest
import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.data.processing.writer.FleetWriter
import com.example.logiroute.data.repository.*
import com.example.logiroute.domain.builder.DomainGraphBuilder
import com.example.logiroute.domain.command.AssignPackageToQueueCommand
import com.example.logiroute.domain.command.DispatchVehicleCommand
import com.example.logiroute.domain.command.StackCommandInvoker
import com.example.logiroute.domain.command.TreeCommandInvoker
import com.example.logiroute.domain.logic.algorithm.routing.*
import com.example.logiroute.domain.logic.algorithm.sorting.PackageSelectionSort
import com.example.logiroute.domain.logic.packagepricing.basepricing.EcoStrategy
import com.example.logiroute.domain.logic.packagepricing.basepricing.RoutePricingEngine
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.request.*
import com.example.logiroute.domain.model.result.VehicleAssignment
import com.example.logiroute.domain.usecase.*

fun main() {
    val loader = Loader()
    val warehouseRepository = CSVWarehouseRepository(loader)
    val packageRepository = CSVPackageRepository(loader, warehouseRepository)
    val routeRepository = CSVRouteRepository(loader, warehouseRepository)
    val vehicleRepository = CSVVehicleRepository(loader, FleetWriter("fleet.csv"), warehouseRepository)

    val graph = try {
        DomainGraphBuilder(packageRepository, routeRepository, warehouseRepository, vehicleRepository).build()
    } catch (e: Exception) {
        println("Failed to build graph: ${e.message}")
        return
    }

    if (graph.packages.isEmpty() || graph.warehouses.isEmpty()) return

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

    println("\n========== USE CASE FLOW ==========")

    val opportunity = detectConsolidation(graph.packages).firstOrNull()

    if (opportunity != null) {
        val packages = prioritizeConsolidation(opportunity)

        val shipment = ShipmentGroupRequest(
            packages = packages,
            origin = opportunity.mainPackage.origin,
            destination = opportunity.mainPackage.destination,
            service = ShipmentService.EXPRESS
        )

        val selectedRoute = selectRoute(shipment)
        val routePackages = packages.filter { it.destination in selectedRoute.path }
        val minCapacity = routePackages.maxOfOrNull { it.weight }

        if (minCapacity != null) {
            val vehicles = findVehicles(FindStationedVehiclesRequest(shipment.origin.id, minCapacity))
            val assignments = rebalanceLoads(assignVehicles(routePackages, vehicles))
            val routeEvaluation = evaluateRoute(selectedRoute)

            println("\nShipment")
            println("Packages: ${routePackages.map { it.id }}")
            println("Route: ${selectedRoute.path.joinToString(" -> ") { it.id }}")
            println("Objective: ${selectedRoute.routingObjective}")

            assignments.forEach {
                val cost = estimateCost(it.vehicle, routeEvaluation)
                val loaded = dispatchVehicle(shipment.origin, it)
                println("${it.vehicle.id}: ${loaded.map { pkg -> pkg.id }} | Cost: $cost")
            }

            val pricing = CalculatePricingUseCase(RoutePricingEngine(EcoStrategy()))
            graph.packages.firstOrNull()?.let {
                println("Package price: ${pricing(it, routeEvaluation.totalDistanceKm)}")
            }
        }
    }

    val source = graph.warehouses.first()
    val destination = graph.warehouses.firstOrNull { it != source }

    if (destination != null) {
        println("\nRouting")
        println("Optimal: ${findOptimalPath(source, destination).joinToString(" -> ") { it.id }}")
        println("Fewest hops: ${findFewestHops(source, destination).joinToString(" -> ") { it.id }}")
    }

    graph.vehicles.firstOrNull()?.let {
        val utilization = calculateUtilization(it)
        println("\nUtilization: ${it.id} = ${utilization.utilizationPercentage}%")
    }

    val loadFactor = GetWarehouseLoadFactorUseCase(warehouseRepository)

    graph.warehouses.firstOrNull { it.stationedVehicles.isNotEmpty() }?.let {
        try {
            println("Warehouse load factor: ${loadFactor(GetWarehouseLoadFactorRequest(it.id))}")
        } catch (_: Exception) {}
    }

    val findBackhaul = FindBackhaulCandidatesUseCase(packageRepository)
    val optimizeBackhaul = OptimizeBackhaulUseCase()

    graph.vehicles.firstOrNull()?.let {
        val returnPath = listOf(it.currentHub)
        val candidates = findBackhaul(it, it.currentHub, returnPath)
        val plan = optimizeBackhaul(it, candidates, returnPath)

        println("\nBackhaul")
        println("Candidates: ${candidates.size}")
        println("Selected: ${plan.selectedPackages.map { pkg -> pkg.id }}")
    }

    val treeReport = AnalyzeTreePerformanceUseCase()()

    println("\nTree")
    println("Unbalanced height: ${treeReport.unbalancedHeight}")
    println("Balanced height: ${treeReport.balancedHeight}")

    val sampleHub = com.example.logiroute.com.example.logiroute.domain.model.request.HubNode(
        warehouse = source,
        hubType = com.example.logiroute.com.example.logiroute.domain.model.request.HubType.GLOBAL_HUB
    )

    println("Lineage: ${TraceHubLineageUseCase()(sampleHub).map { it.warehouse.id }}")

    val detectEmergency = DetectEmergencyCargoRescueOpportunitiesUseCase(
        packageRepository, vehicleRepository, warehouseRepository, findOptimalPath
    )
    val executeEmergency = ExecuteEmergencyCargoPrioritizationUseCase(packageRepository)

    graph.packages.firstOrNull { it.priority == Priority.URGENT }?.let { urgent ->
        try {
            detectEmergency(DetectEmergencyCargoRescueRequest(urgent.origin.id)).firstOrNull()?.let {
                val plan = executeEmergency(ExecuteEmergencyCargoPrioritizationRequest(it))
                println("\nEmergency: ${plan.loadedUrgentPackages.map { pkg -> pkg.id }}")
            }
        } catch (_: Exception) {}
    }

    val assignToQueue = AssignPackageToCargoQueueUseCase()
    val samplePackage = graph.packages.first()

    println("\nQueue assignment: ${assignToQueue(samplePackage.origin, samplePackage)}")

    val addVehicle = AddVehicleToHubUseCase(vehicleRepository)
    graph.vehicles.firstOrNull()?.let { println("Vehicle added: ${addVehicle(it)}") }

    val reroutePackage = ReroutePackageUseCase(packageRepository, warehouseRepository)

    graph.packages.firstOrNull()?.let { pkg ->
        graph.warehouses.firstOrNull { it != pkg.destination }?.let { newDestination ->
            try {
                val rerouted = reroutePackage(pkg.id, newDestination.id)
                println("Rerouted: ${rerouted.id} -> ${rerouted.destination.id}")
            } catch (_: Exception) {}
        }
    }

    val testWarehouse = graph.warehouses.firstOrNull() ?: return
    val template = graph.packages.firstOrNull() ?: return
    val initialQueue = testWarehouse.cargoQueue.toList()

    println("\n========== SUBTASK 5: STACK VS TREE ==========")

    val stack = StackCommandInvoker()

    val stackA = AssignPackageToQueueCommand(
        assignToQueue, testWarehouse, template.copy(id = "STACK-A", origin = testWarehouse)
    )
    val stackB = AssignPackageToQueueCommand(
        assignToQueue, testWarehouse, template.copy(id = "STACK-B", origin = testWarehouse)
    )
    val stackC = AssignPackageToQueueCommand(
        assignToQueue, testWarehouse, template.copy(id = "STACK-C", origin = testWarehouse)
    )

    println("\n--- STACK ---")
    println("Empty -> Undo: ${stack.undo()} | Redo: ${stack.redo()}")

    stack.executeCommand(stackA)
    stack.executeCommand(stackB)

    println("Execute A & B -> History: ${stack.historySize()}")

    stack.undo()
    println("Undo B -> History: ${stack.historySize()}")

    stack.executeCommand(stackC)

    println("Execute C after Undo")
    println("Redo old B: ${stack.redo()}")
    println("STACK RESULT -> old B future is lost")

    testWarehouse.restoreCargoQueue(initialQueue)

    val tree = TreeCommandInvoker()

    val treeA = AssignPackageToQueueCommand(
        assignToQueue, testWarehouse, template.copy(id = "TREE-A", origin = testWarehouse)
    )
    val treeB = AssignPackageToQueueCommand(
        assignToQueue, testWarehouse, template.copy(id = "TREE-B", origin = testWarehouse)
    )
    val treeC = AssignPackageToQueueCommand(
        assignToQueue, testWarehouse, template.copy(id = "TREE-C", origin = testWarehouse)
    )

    println("\n--- TREE ---")
    println("Empty -> Undo: ${tree.undo()} | Redo: ${tree.redo()}")

    tree.executeCommand(treeA)
    tree.executeCommand(treeB)

    println("Execute A & B -> History: ${tree.historySize()}")

    tree.undo()
    println("Undo B -> History: ${tree.historySize()}")

    tree.executeCommand(treeC)
    println("Execute C after Undo -> History: ${tree.historySize()}")

    tree.undo()

    println("Branches from A: ${tree.branchCount()}")
    println("Branch 0 -> old B")
    println("Branch 1 -> new C")
    println("Redo old B branch: ${tree.redo(0)}")
    println("TREE RESULT -> B and C are both preserved")

    testWarehouse.restoreCargoQueue(initialQueue)

    println("\n========== STACK VS TREE VISUAL ==========")

    println(
        """
STACK:

    A -> B
    Undo B
    Execute C

    A -> C

    B is LOST


TREE:

       A
      / \
     B   C

    B and C are PRESERVED
        """.trimIndent()
    )

    println("\n========== DISPATCH UNDO / REDO ==========")

    val baseVehicle = graph.vehicles.firstOrNull() ?: return

    val stackWarehouse = testWarehouse.copy()
    val stackVehicle = baseVehicle.copy(
        id = "STACK-DISPATCH",
        currentHub = stackWarehouse,
        loadedPackages = mutableListOf()
    )
    val stackPkg = template.copy(id = "STACK-DISPATCH-PKG", origin = stackWarehouse)

    stackWarehouse.addPackage(stackPkg)

    val stackAssignment = VehicleAssignment(
        vehicle = stackVehicle,
        packages = listOf(stackPkg),
        totalWeightKg = stackPkg.weight,
        remainingCapacityKg = stackVehicle.maxCapacityKg - stackPkg.weight
    )

    val stackDispatchInvoker = StackCommandInvoker()
    val stackDispatchCommand = DispatchVehicleCommand(dispatchVehicle, stackWarehouse, stackAssignment)

    println("\n--- STACK DISPATCH ---")
    println("Before -> Warehouse: ${stackWarehouse.cargoQueue.size} | Vehicle: ${stackVehicle.loadedPackages.size}")

    stackDispatchInvoker.executeCommand(stackDispatchCommand)
    println("Execute -> Warehouse: ${stackWarehouse.cargoQueue.size} | Vehicle: ${stackVehicle.loadedPackages.size}")

    stackDispatchInvoker.undo()
    println("Undo -> Warehouse: ${stackWarehouse.cargoQueue.size} | Vehicle: ${stackVehicle.loadedPackages.size}")

    stackDispatchInvoker.redo()
    println("Redo -> Warehouse: ${stackWarehouse.cargoQueue.size} | Vehicle: ${stackVehicle.loadedPackages.size}")

    val treeWarehouse = testWarehouse.copy()
    val treeVehicle = baseVehicle.copy(
        id = "TREE-DISPATCH",
        currentHub = treeWarehouse,
        loadedPackages = mutableListOf()
    )
    val treePkg = template.copy(id = "TREE-DISPATCH-PKG", origin = treeWarehouse)

    treeWarehouse.addPackage(treePkg)

    val treeAssignment = VehicleAssignment(
        vehicle = treeVehicle,
        packages = listOf(treePkg),
        totalWeightKg = treePkg.weight,
        remainingCapacityKg = treeVehicle.maxCapacityKg - treePkg.weight
    )

    val treeDispatchInvoker = TreeCommandInvoker()
    val treeDispatchCommand = DispatchVehicleCommand(dispatchVehicle, treeWarehouse, treeAssignment)

    println("\n--- TREE DISPATCH ---")
    println("Before -> Warehouse: ${treeWarehouse.cargoQueue.size} | Vehicle: ${treeVehicle.loadedPackages.size}")

    treeDispatchInvoker.executeCommand(treeDispatchCommand)
    println("Execute -> Warehouse: ${treeWarehouse.cargoQueue.size} | Vehicle: ${treeVehicle.loadedPackages.size}")

    treeDispatchInvoker.undo()
    println("Undo -> Warehouse: ${treeWarehouse.cargoQueue.size} | Vehicle: ${treeVehicle.loadedPackages.size}")

    treeDispatchInvoker.redo()
    println("Redo -> Warehouse: ${treeWarehouse.cargoQueue.size} | Vehicle: ${treeVehicle.loadedPackages.size}")

    println("\n========== DONE ==========")
}