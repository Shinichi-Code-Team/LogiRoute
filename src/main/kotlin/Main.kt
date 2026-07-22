import com.example.logiroute.dataholder.*
import com.example.logiroute.logic.parser.*
import com.example.logiroute.logic.sorting.*


fun printTopPackages(packages: List<PackageRow>) {

    println("Successfully parsed packages: ${packages.size}")

    for (i in 0 until minOf(3, packages.size)) {

        val packageRow = packages[i]
        println("------------Top 3 Priority packages-------------")
        println(
            "ID: ${packageRow.id}, " +
                    "Weight: ${packageRow.weight}, " +
                    "Destination: ${packageRow.destinationHubId}, " +
                    "Priority: ${packageRow.priority}"
        )
    }
}

fun processPackages() {

    val packages = packageParser()
    sortPackagesByPriority(packages)
    printTopPackages(packages)
}

fun printSampleRoutes(routes: List<RouteRow>) {


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
    val routes = routeParser()
    printSampleRoutes(routes)
}

fun printSampleFleet(fleetList: List<FleetRow>) {
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
    val fleetList = parseFleetCsv("fleet.csv")
    printSampleFleet(fleetList)
}

fun printSampleWarehouses(warehouses: List<WarehouseRow>) {


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
    val warehouses = warehouseParser()
    printSampleWarehouses(warehouses)
}


fun main() {
    println("------------------------Packages section------------------------")
    processPackages()
    println("\n------------------------ Routes section-------------------------")
    processRoutes()
    println("\n------------------------ Fleets section-------------------------")
    processFleet()
    println("\n------------------------ Warehouses section-------------------------")
    processWarehouses()

}

