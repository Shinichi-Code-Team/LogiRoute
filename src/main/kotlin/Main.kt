package com.example.logiroute

import com.example.logiroute.com.example.logiroute.domain.model.request.GetWarehouseLoadFactorRequest
import com.example.logiroute.com.example.logiroute.domain.usecase.model.request.FindStationedVehiclesRequest
import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.data.processing.writer.FleetWriter
import com.example.logiroute.data.repository.*
import com.example.logiroute.domain.builder.DomainGraphBuilder
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

    val graph = DomainGraphBuilder(
        packageRepository,
        routeRepository,
        warehouseRepository,
        vehicleRepository
    ).build()

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
        runCatching {
            loadFactorUseCase(GetWarehouseLoadFactorRequest(it.id))
        }.onSuccess { factor ->
            println("Warehouse load factor: $factor")
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
        runCatching {
            detectEmergency(DetectEmergencyCargoRescueRequest(urgent.origin.id))
        }.onSuccess { rescue ->
            rescue.firstOrNull()?.let {
                val plan = executeEmergency(ExecuteEmergencyCargoPrioritizationRequest(it))
                println("\nEmergency: ${plan.loadedUrgentPackages.map { pkg -> pkg.id }}")
            }
        }
    }

    val assignToQueue = AssignPackageToCargoQueueUseCase()
    val samplePackage = graph.packages.first().copy(id = "TEST-PACKAGE")

    val addedToQueue = assignToQueue(samplePackage.origin, samplePackage)
    println("\nQueue assignment: $addedToQueue")

    val addVehicle = AddVehicleToHubUseCase(vehicleRepository)

    graph.vehicles.firstOrNull()?.let {
        val sampleVehicle = it.copy(id = "TEST-VEHICLE")
        println("Vehicle added: ${addVehicle(sampleVehicle)}")
    }

    val reroutePackage = ReroutePackageUseCase(packageRepository, warehouseRepository)

    graph.packages.firstOrNull()?.let { pkg ->
        graph.warehouses.firstOrNull { it != pkg.destination }?.let { newDestination ->
            runCatching {
                reroutePackage(pkg.id, newDestination.id)
            }.onSuccess {
                println("Rerouted: ${it.id} -> ${it.destination.id}")
            }
        }
    }

    println("\nDone")
}