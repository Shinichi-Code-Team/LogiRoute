import com.example.logiroute.data.dataholder.*
import com.example.logiroute.data.processing.parser.*
import com.example.logiroute.logic.sortPackagesByPriorityConsideringWeight

const val SAMPLE_SIZE = 3

fun printTopPackages(sortedPackages: List<PackageRaw>) {
    println("Successfully parsed packages: ${sortedPackages.size}")
    println("------------Top 3 Priority packages-------------")
    for (sortedPackage in sortedPackages.take(SAMPLE_SIZE)) {
        println(sortedPackage)
    }
}

fun processPackages(): List<PackageRaw> {
    val lines = readCsvLines("packages.csv")
    val packages = parsePackages(lines)
    return sortPackagesByPriorityConsideringWeight(packages)
}

fun printSampleRoutes(routes: List<RouteRaw>) {
    println("Successfully parsed routes: ${routes.size}")
    for (route in routes.take(SAMPLE_SIZE)) {
        println(route)
    }
}

fun processRoutes(): List<RouteRaw> {
    val lines = readCsvLines("routes.csv")
    return parseRoutes(lines)
}

fun printSampleFleet(fleetList: List<FleetRaw>) {
    println("Successfully parsed fleet records count: ${fleetList.size}")
    for (fleet in fleetList.take(SAMPLE_SIZE)) {
        println(fleet)
    }
}

fun processFleet(): List<FleetRaw> {
    val lines = readCsvLines("fleet.csv")
    return parseFleets(lines)
}

fun printSampleWarehouses(warehouses: List<WarehouseRaw>) {
    println("Successfully parsed warehouses: ${warehouses.size}")
    for (warehouse in warehouses.take(SAMPLE_SIZE)) {
        println(warehouse)
    }
}

fun processWarehouses(): List<WarehouseRaw> {
    val lines = readCsvLines("warehouses.csv")
    return parseWarehouses(lines)
}

fun main() {
    println("------------------------Packages section------------------------")
    printTopPackages(processPackages())

    println("\n------------------------ Routes section-------------------------")
    printSampleRoutes(processRoutes())

    println("\n------------------------ Fleets section-------------------------")
    printSampleFleet(processFleet())

    println("\n------------------------ Warehouses section-------------------------")
    printSampleWarehouses(processWarehouses())
}