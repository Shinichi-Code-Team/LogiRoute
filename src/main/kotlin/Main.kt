import com.example.logiroute.data.processing.loader.*
import com.example.logiroute.domain.builder.DomainGraph
import com.example.logiroute.domain.builder.DomainGraphBuilder
import com.example.logiroute.domain.builder.DomainGraphInput
import com.example.logiroute.domain.logic.algorithm.*
import com.example.logiroute.domain.logic.pricing.*
import com.example.logiroute.domain.model.*
import com.example.logiroute.domain.service.PackageAssignmentRing

private const val SAMPLE_SIZE = 5

fun main() {
    val input = loadInputData()

    printRawDataSummary(input)

    val domainGraph = DomainGraphBuilder().build(input)

    printDomainGraphSummary(domainGraph)

    val firstWarehouse = domainGraph.warehouses.firstOrNull()

    if (firstWarehouse == null) {
        println("No valid warehouses were found.")
        return
    }

    printWarehouseSummary(firstWarehouse)

    printSelectionSortResult(domainGraph.packages)

    val sortedCargoQueue = runQuickSort(firstWarehouse)

    runPricingDemo(
        warehouse = firstWarehouse,
        sortedCargoQueue = sortedCargoQueue
    )
}

private fun loadInputData(): DomainGraphInput {
    return DomainGraphInput(
        warehouseRaws = loadWarehouses(),
        packageRaws = loadPackages(),
        routeRaws = loadRoutes(),
        fleetRaws = loadFleets()
    )
}

private fun printRawDataSummary(input: DomainGraphInput) {
    println("\n================ RAW DATA ================")
    println("Warehouses: ${input.warehouseRaws.size}")
    println("Packages: ${input.packageRaws.size}")
    println("Routes: ${input.routeRaws.size}")
    println("Fleet records: ${input.fleetRaws.size}")
}

private fun printDomainGraphSummary(domainGraph: DomainGraph) {
    println("\n================ DOMAIN GRAPH ================")
    println("Warehouses: ${domainGraph.warehouses.size}")
    println("Packages: ${domainGraph.packages.size}")
    println("Routes: ${domainGraph.routes.size}")
    println("Vehicles: ${domainGraph.vehicles.size}")
}

private fun printWarehouseSummary(warehouse: Warehouse) {
    println("\n================ FIRST WAREHOUSE ================")
    println("Warehouse ID: ${warehouse.id}")
    println("Warehouse name: ${warehouse.name}")
    println("Cargo queue size: ${warehouse.cargoQueue.size}")
    println("Outgoing routes: ${warehouse.outgoingRoutes.size}")
    println("Stationed vehicles: ${warehouse.stationedVehicles.size}")
}

private fun printSelectionSortResult(packages: List<Package>) {
    println("\n================ SELECTION SORT ================")

    val sortedPackages =
        sortPackagesByPriorityConsideringWeight(packages)

    for (packageItem in sortedPackages.take(SAMPLE_SIZE)) {
        println(
            "Package ID: ${packageItem.id}, " +
                    "Priority: ${packageItem.priority}, " +
                    "Weight: ${packageItem.weight}"
        )
    }

    val isSortedCorrectly =
        sortedPackages.zipWithNext().all { (currentPackage, nextPackage) ->
            currentPackage.priority > nextPackage.priority ||
                    currentPackage.priority == nextPackage.priority &&
                    currentPackage.weight >= nextPackage.weight
        }

    println("Selection Sort correct: $isSortedCorrectly")
}

private fun runQuickSort(
    warehouse: Warehouse
): MutableList<Package> {
    println("\n================ QUICK SORT ================")

    val cargoQueue = warehouse.cargoQueue.toMutableList()

    if (cargoQueue.isEmpty()) {
        println("The first warehouse has no packages.")
        return cargoQueue
    }

    println("Before sorting:")

    for (packageItem in cargoQueue.take(SAMPLE_SIZE)) {
        println(
            "Package ID: ${packageItem.id}, " +
                    "Weight: ${packageItem.weight}"
        )
    }

    sortByWeightDescending(cargoQueue)

    println("\nAfter sorting:")

    for (packageItem in cargoQueue.take(SAMPLE_SIZE)) {
        println(
            "Package ID: ${packageItem.id}, " +
                    "Weight: ${packageItem.weight}"
        )
    }

    val isSortedCorrectly =
        cargoQueue.zipWithNext().all { (currentPackage, nextPackage) ->
            currentPackage.weight >= nextPackage.weight
        }

    println("Quick Sort correct: $isSortedCorrectly")

    return cargoQueue
}

private fun runPricingDemo(
    warehouse: Warehouse,
    sortedCargoQueue: List<Package>
) {
    println("\n================ PRICING STRATEGIES ================")

    val selectedPackage = sortedCargoQueue.firstOrNull()
    val selectedRoute = warehouse.outgoingRoutes.firstOrNull()

    if (selectedPackage == null) {
        println("No package is available for pricing.")
        return
    }

    if (selectedRoute == null) {
        println("No outgoing route is available for pricing.")
        return
    }

    println("Package ID: ${selectedPackage.id}")
    println("Package weight: ${selectedPackage.weight}")
    println("Package priority: ${selectedPackage.priority}")
    println("Route ID: ${selectedRoute.id}")
    println("Route distance: ${selectedRoute.distanceKm} km")

    val pricingEngine = RoutePricingEngine(EcoStrategy())

    val ecoCost = pricingEngine.computeFinalCost(
        distanceKm = selectedRoute.distanceKm,
        weight = selectedPackage.weight,
        priority = selectedPackage.priority
    )

    println("\nEco Strategy cost: $ecoCost")

    pricingEngine.switchStrategy(ExpressStrategy())

    val expressCost = pricingEngine.computeFinalCost(
        distanceKm = selectedRoute.distanceKm,
        weight = selectedPackage.weight,
        priority = selectedPackage.priority
    )

    println("Express Strategy cost: $expressCost")

    //================================================

    println("---> Test Package Assignment Ring <---")

    fun runRingTestWithCsvLimited() {
        val warehouseRaws = loadWarehouses()
        val packageRaws = loadPackages()
        val fleetRaws = loadFleets()
        val routeRaws = loadRoutes()

        val input = DomainGraphInput(
            warehouseRaws = warehouseRaws,
            packageRaws = packageRaws,
            fleetRaws = fleetRaws,
            routeRaws = routeRaws
        )

        val domainGraph = DomainGraphBuilder().build(input)
        val vehicles = domainGraph.vehicles
        val packages = domainGraph.packages

        val ring = PackageAssignmentRing(vehicles)

        val limitedPackages = packages.take(5)
        val initialAssignments = ring.assignPackagesToVehicles(limitedPackages)
        println("Initial Assignments (limited): $initialAssignments")

        val updatedAssignments = ring.reassignPackagesAfterBreakdown(initialAssignments, 40)
        println("Updated Assignments after breakdown (limited): $updatedAssignments")

        val isStable = ring.assertStableAssignments(initialAssignments, updatedAssignments)
        println("Assignments stable for other vehicles: $isStable")
    }

    println("---> Test Package Assignment Ring <---")
    runRingTestWithCsvLimited()

    // Test 2
    fun runRingTest() {
        val vehicles = listOf(
            Vehicle("V1", 1000.0, 5.0, Warehouse("W1", "Main", "ZoneA", 0.0, 0.0)),
            Vehicle("V2", 1200.0, 6.0, Warehouse("W2", "Second", "ZoneB", 0.0, 0.0)),
            Vehicle("V3", 1500.0, 7.0, Warehouse("W3", "Third", "ZoneC", 0.0, 0.0)),
            Vehicle("V4", 2000.0, 8.0, Warehouse("W4", "Fourth", "ZoneD", 0.0, 0.0))
        )

        val packages = listOf(
            Package("P1", 10.0, vehicles[0].currentHub, vehicles[1].currentHub, Priority.URGENT),
            Package("P2", 20.0, vehicles[1].currentHub, vehicles[2].currentHub, Priority.STANDARD),
            Package("P3", 30.0, vehicles[2].currentHub, vehicles[3].currentHub, Priority.LOW)
        )

        val ring = PackageAssignmentRing(vehicles)

        val initialAssignments = ring.assignPackagesToVehicles(packages)
        println("Initial Assignments: $initialAssignments")

        val updatedAssignments = ring.reassignPackagesAfterBreakdown(initialAssignments, 40)
        println("Updated Assignments after breakdown: $updatedAssignments")

        val isStable = ring.assertStableAssignments(initialAssignments, updatedAssignments)
        println("Assignments stable for other vehicles: $isStable")
    }

    runRingTest()
}