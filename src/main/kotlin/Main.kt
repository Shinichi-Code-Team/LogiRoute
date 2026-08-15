import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.data.repository.CSVPackageRepository
import com.example.logiroute.data.repository.CSVRouteRepository
import com.example.logiroute.data.repository.CSVVehicleRepository
import com.example.logiroute.data.repository.CSVWarehouseRepository
import com.example.logiroute.domain.builder.DomainGraph
import com.example.logiroute.domain.builder.DomainGraphBuilder
import com.example.logiroute.domain.builder.DomainGraphInput
import com.example.logiroute.domain.logic.algorithm.sortByWeightDescending
import com.example.logiroute.domain.logic.algorithm.sortPackagesByPriorityConsideringWeight
import com.example.logiroute.domain.logic.packagepricing.basepricing.EcoStrategy
import com.example.logiroute.domain.logic.packagepricing.basepricing.ExpressStrategy
import com.example.logiroute.domain.logic.packagepricing.basepricing.RoutePricingEngine
import com.example.logiroute.domain.logic.packagepricing.servicepricing.ColdChainDecorator
import com.example.logiroute.domain.logic.packagepricing.servicepricing.DecoratedPackagePricingService
import com.example.logiroute.domain.logic.packagepricing.servicepricing.ExpressInsuranceDecorator
import com.example.logiroute.domain.logic.packagepricing.servicepricing.FragileHandlingDecorator
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.service.PackageAssignmentRing
import com.example.logiroute.domain.service.PackageAssignmentRing2

private const val SAMPLE_SIZE = 5

val loader = Loader()
val packagesRepository = CSVPackageRepository(loader = loader)
val routesRepository = CSVRouteRepository(loader = loader)
val warehouseRepository = CSVWarehouseRepository(loader = loader)
val vehicleRepository = CSVVehicleRepository(loader = loader)

fun main() {
    val input = loadInputData()
    printRawDataSummary(input)
    val domainGraph =
        DomainGraphBuilder(packagesRepository, routesRepository, warehouseRepository, vehicleRepository).build(input)
    printDomainGraphSummary(domainGraph)
    val firstWarehouse = domainGraph.warehouses.firstOrNull()
    if (firstWarehouse == null) {
        println("No valid warehouses were found.")
        return
    }
    printWarehouseSummary(firstWarehouse)
    printSelectionSortResult(domainGraph.packages)
    val sortedCargoQueue = runQuickSort(firstWarehouse)
    runPricingDemo(warehouse = firstWarehouse, sortedCargoQueue = sortedCargoQueue)
    runRingTestWithCsvLimited2()

    println("--------------Package Decorator Test---------------")
    val warehouseA = Warehouse(
        id = "WH-1",
        name = "Global Hub",
        regionalZone = "North",
        latitude = 31.5,
        longitude = 34.4
    )

    val warehouseB = Warehouse(
        id = "WH-2",
        name = "Regional Center",
        regionalZone = "South",
        latitude = 31.4,
        longitude = 34.3
    )

    val packageItem = Package(
        id = "PKG-1",
        weight = 10.0,
        origin = warehouseA,
        destination = warehouseB,
        priority = Priority.URGENT
    )
    val pricingEngine = RoutePricingEngine(ExpressStrategy())
    val pricingService = DecoratedPackagePricingService(pricingEngine)
    val basePackageCost = pricingService.calculatePackageCost(
        packageItem,
        100.0,
        weight = packageItem.weight,
        priority = packageItem.priority
    )
    println("Base Package Cost = $basePackageCost")
    val premiumPackage =
        ColdChainDecorator(
            ExpressInsuranceDecorator(
                FragileHandlingDecorator(packageItem)
            )
        )

    val finalCost = pricingService.calculatePackageCost(
        packageComponent = premiumPackage,
        distanceKm = 100.0,
        weight = packageItem.weight,
        priority = packageItem.priority
    )

    println("Decorated Cost = $finalCost")
}

private fun loadInputData(): DomainGraphInput {
    return DomainGraphInput(
        warehouseRaws = warehouseRepository.getWarehouses(),
        packageRaws = packagesRepository.getPackages(),
        routeRaws = routesRepository.getRoutes(),
        vehicleRaws = loader.loadFleets()
    )
}

private fun printRawDataSummary(input: DomainGraphInput) {
    println("\n================ RAW DATA ================")
    println("Warehouses: ${input.warehouseRaws.size}")
    println("Packages: ${input.packageRaws.size}")
    println("Routes: ${input.routeRaws.size}")
    println("Fleet records: ${input.vehicleRaws.size}")
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
    val sortedPackages = sortPackagesByPriorityConsideringWeight(packages)
    for (packageItem in sortedPackages.take(SAMPLE_SIZE)) {
        println("Package ID: ${packageItem.id}, " + "Priority: ${packageItem.priority}, " + "Weight: ${packageItem.weight}")
    }

    val isSortedCorrectly =
        sortedPackages.zipWithNext().all { (currentPackage, nextPackage) ->
            currentPackage.priority > nextPackage.priority ||
                    currentPackage.priority == nextPackage.priority &&
                    currentPackage.weight >= nextPackage.weight
        }

    println("Selection Sort correct: $isSortedCorrectly")
}

private fun runQuickSort(warehouse: Warehouse): MutableList<Package> {
    println("\n================ QUICK SORT ================")
    val cargoQueue = warehouse.cargoQueue.toMutableList()
    if (cargoQueue.isEmpty()) {
        println("The first warehouse has no packages.")
        return cargoQueue
    }
    println("Before sorting:")
    for (packageItem in cargoQueue.take(SAMPLE_SIZE)) {
        println("Package ID: ${packageItem.id}, " + "Weight: ${packageItem.weight}")
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

private fun runPricingDemo(warehouse: Warehouse, sortedCargoQueue: List<Package>) {
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


        val input = DomainGraphInput(
            packageRaws = packagesRepository.getPackages(),
            routeRaws = routesRepository.getRoutes(),
            warehouseRaws = warehouseRepository.getWarehouses(),
            vehicleRaws = vehicleRepository.getVehicles()
        )

        val domainGraph = DomainGraphBuilder(
            packagesRepository,
            routesRepository,
            warehouseRepository,
            vehicleRepository
        ).build(input)
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
    runRingTestWithCsvLimited2()

}

fun runRingTestWithCsvLimited2() {
    val warehouseRaws = warehouseRepository.getWarehouses()
    val packageRaws = packagesRepository.getPackages()
    val fleetRaws = loader.loadFleets()
    val routeRaws = routesRepository.getRoutes()

    val input = DomainGraphInput(
        warehouseRaws = warehouseRaws,
        packageRaws = packageRaws,
        vehicleRaws = fleetRaws,
        routeRaws = routeRaws
    )
    val domainGraph = DomainGraphBuilder(
        packageRepository = packagesRepository,
        routeRepository = routesRepository,
        warehouseRepository = warehouseRepository,
        vehicleRepository = vehicleRepository
    ).build(input)
    val vehicles = domainGraph.vehicles
    val packages = domainGraph.packages
    val ring = PackageAssignmentRing2()
    ring.addVehicle(15, vehicles[0])
    ring.addVehicle(40, vehicles[1])
    ring.addVehicle(65, vehicles[2])
    ring.addVehicle(90, vehicles[3])

    val limitedPackages = packages.take(70)
    ring.assignPackages(limitedPackages)
    println("===== INITIAL ASSIGNMENTS =====")
    for ((slot, assignedPackages) in ring.getAssignments()) {
        println("Slot $slot -> ${assignedPackages.size} packages")
    }
    val initialAssignments = ring.copyAssignments()
    val brokenPackages = ring.removeVehicle(40)
    println()
    println("Vehicle at slot 40 BROKEN")
    println("Packages to reroute: ${brokenPackages.size}")
    val unassignedPackages = ring.reroutePackages(brokenPackages)
    println()
    println("===== UPDATED ASSIGNMENTS =====")
    for ((slot, assignedPackages) in ring.getAssignments()) {
        println("Slot $slot -> ${assignedPackages.size} packages")
    }
    println()
    println("Could not be assigned because of capacity: " + unassignedPackages.size)
}


