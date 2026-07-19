package org.example.com.example.logiroute.models

import java.io.File

data class Fleet(
        val vehicleId: String,
        val currentHubId: String,
        val maxCapacityKg: Double,
        val costPerKm: Double
)
fun parseFleet(fileName: String): List<Fleet> {
        val fleetList = mutableListOf<Fleet>()
        val lines = readCsvLines(fileName)

        for (line in lines.drop(1)) {
                if (isValidRow(line, 4)) {
                        val parts = splitAndTrim(line)


                        val vehicleId = parts[0]
                        val currentHubId = parts[1]
                        val maxCapacityKg = parts[2].toDoubleSafe()
                        val costPerKm = parts[3].toDoubleSafe()

                        fleetList.add(Fleet(vehicleId, currentHubId, maxCapacityKg, costPerKm))
                }
        }
        return fleetList
}



