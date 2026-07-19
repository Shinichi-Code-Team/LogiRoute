package org.example

import java.io.File
import org.example.com.example.logiroute.models.parseFleet

fun main() {
    val fleetFile = File("src/main/resources/fleet.csv")

    val loadedFleets = parseFleet(fleetFile.name)

    println("Fleets loaded: ${loadedFleets.size}")

    for (i in 0 until loadedFleets.size) {
        val f = loadedFleets[i]
        println("Fleet ${i + 1} | ID: ${f.vehicleId} | Hub: ${f.currentHubId} | Capacity: ${f.maxCapacityKg} | Cost: ${f.costPerKm}")
    }
}