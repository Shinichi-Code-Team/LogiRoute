import com.example.logiroute.data.dataholder.*
import com.example.logiroute.data.processing.parser.*
import com.example.logiroute.logic.sortPackagesByPriorityConsideringWeight

const val SAMPLE_SIZE = 3
fun printTopPackages(sortedPackages: List<PackageRaw>) {
    println("Successfully parsed packages: ${sortedPackages.size}")
    println("------------Top 3 Priority packages-------------")
    for (sortedPackage in sortedPackages.take(SAMPLE_SIZE)) {
        println(sortedPackage.toString())
    }
}

fun processPackages(): List<PackageRaw> {
    val lines = readCsvLines("packages.csv")
    val packages = parsePackages(lines)
    val sortedPackages =
        sortPackagesByPriorityConsideringWeight(packages)
    return sortedPackages
}

fun printSampleRoutes(routes: List<RouteRaw>) {


    println("Successfully parsed routes: ${routes.size}")
    for (i in 0 until minOf(3, routes.size)) {
        val route = routes[i]
        println(
            "Route ID: ${route.routeId}, " +
                    "Origin: ${route.originHubId}, " +
                    "Destination: ${route.destinationHubId}, " +
                    "Distance: ${route.distanceKm} km, " +
                    "Delay: ${route.typicalDelayMin} min"
        )
    }
}


fun processRoutes() {
    val routes = parseRoutes()
    printSampleRoutes(routes)
}

fun printSampleFleet(fleetList: List<FleetRaw>) {
    println("Successfully parsed fleet records count: ${fleetList.size}")

    for (i in 0 until minOf(3, fleetList.size)) {
        val fleet = fleetList[i]

        println(
            "Vehicle ID: ${fleet.vehicleId[0]}, " +
                    "Hub: ${fleet.currentHubId}, " +
                    "Capacity: ${fleet.maxCapacityKg} kg, " +
                    "Cost/Km: ${fleet.costPerKm}"
        )
    }
}

fun processFleet() {
    val fleetList = parseFleets("fleet.csv")
    printSampleFleet(fleetList)
}

fun printSampleWarehouses(warehouses: List<WarehouseRaw>) {

    println("Successfully parsed routes: ${warehouses.size}")
    for (i in 0 until minOf(3, warehouses.size)) {
        val warehouse = warehouses[i]
        println(
            "warehouse ID: ${warehouse.id}, " +
                    "warehouse name: ${warehouse.name}, " +
                    "warehouse regionalZone: ${warehouse.regionalZone}, "
        )
    }
}


fun processWarehouses() {
    val lines = readCsvLines("warehouses.csv")
    val warehouses = parseWarehouses(lines)
    printSampleWarehouses(warehouses)
}


fun main() {
    println("------------------------Packages section------------------------")
   printTopPackages(processPackages())

    println("\n------------------------ Routes section-------------------------")
    processRoutes()
    println("\n------------------------ Fleets section-------------------------")
    processFleet()
    println("\n------------------------ Warehouses section-------------------------")
    processWarehouses()

}

