import com.example.logiroute.data.dataholder.*
import com.example.logiroute.data.processing.loader.*
import com.example.logiroute.domain.logic.sortPackagesByPriorityConsideringWeight

const val SAMPLE_SIZE = 3
val packagesRaw = loaderPackages()
val sortedPackages = sortPackagesByPriorityConsideringWeight(packagesRaw)
fun printTopPackages(sortedPackages: List<PackageRaw>) {
    println("Successfully parsed packages: ${sortedPackages.size}")
    println("------------Top 3 Priority packages-------------")
    for (sortedPackage in sortedPackages.take(SAMPLE_SIZE)) {
        println(sortedPackage)
    }
}

val routesRaw = loaderRoutes()
fun printSampleRoutes(routes: List<RouteRaw>) {
    println("Successfully parsed routes: ${routes.size}")
    for (route in routes.take(SAMPLE_SIZE)) {
        println(route)
    }
}


val fleetsRaw = loaderFleet()
fun printSampleFleet(fleetList: List<FleetRaw>) {
    println("Successfully parsed fleet records count: ${fleetList.size}")
    for (fleet in fleetList.take(SAMPLE_SIZE)) {
        println(fleet)
    }
}

val warehousesRaw = loaderWarehouses()
fun printSampleWarehouses(warehouses: List<WarehouseRaw>) {
    println("Successfully parsed warehouses: ${warehouses.size}")
    for (warehouse in warehouses.take(SAMPLE_SIZE)) {
        println(warehouse)
    }
}


fun main() {
    println("------------------------Packages section------------------------")
    printTopPackages(sortedPackages)

    println("\n------------------------ Routes section-------------------------")
    printSampleRoutes(routesRaw)

    println("\n------------------------ Fleets section-------------------------")
    printSampleFleet(fleetsRaw)

    println("\n------------------------ Warehouses section-------------------------")
    printSampleWarehouses(warehousesRaw)
}