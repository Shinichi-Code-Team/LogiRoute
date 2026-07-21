package com.example.logiroute


import com.example.logiroute.dataholder.FleetRow
import com.example.logiroute.dataholder.PackageRow
import com.example.logiroute.logic.parser.routeParser
import com.example.logiroute.logic.sorting.sortPackagesByPriority
import org.example.com.example.logiroute.dataholder.RouteRow
import com.example.logiroute.logic.parser.parseFleetCsv

import packageParser

fun printTopPackages(packages: List<PackageRow>) {

    println("Successfully parsed packages: ${packages.size}")

    for (i in 0 until minOf(3, packages.size)) {

        val packageRow = packages[i]

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

fun main() {
    processPackages()
    processRoutes()
    processFleet()

}

