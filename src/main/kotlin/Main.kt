package com.example.logiroute

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
import com.example.logiroute.domain.logic.algorithm.sorting.PackageSelectionSort
import com.example.logiroute.domain.model.request.ShipmentGroupRequest
import com.example.logiroute.domain.model.request.ShipmentService
import com.example.logiroute.domain.usecase.*

fun main() {
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
}