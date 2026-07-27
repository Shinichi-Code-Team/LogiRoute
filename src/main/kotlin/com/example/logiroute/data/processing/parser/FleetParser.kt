package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.dataholder.FleetRaw
import com.example.logiroute.data.processing.validation.*


fun parseFleets(fileName: String): List<FleetRaw> {
    val lines = readCsvLines(fileName)
    if (lines.isEmpty()) return emptyList()

    val expectedColumnCount = getExpectedColumnCount(lines.first())
    val dataLines = skipHeader(lines)
    val fleetList = mutableListOf<FleetRaw>()

    for (line in dataLines) {
        if (!isNotBlank(line)) continue

        val columns = splitAndTrim(line)
        if (hasValidColumnCount(columns, line, expectedColumnCount)) continue

        val maxCapacityKg = extractAndValidateMaxCapacity(columns, line)
        if (maxCapacityKg == DEFAULT_INVALID_DOUBLE) continue

        val costPerKm = extractAndValidateCostPerKm(columns, line)
        if (costPerKm == DEFAULT_INVALID_DOUBLE) continue

        val vehicleId = listOf(columns[0])
        val currentHubId = columns[1]
        val fleetRaw = createFleetObject(vehicleId, currentHubId, maxCapacityKg, costPerKm)
        fleetList.add(fleetRaw)
    }

    return fleetList
}

private fun hasValidColumnCount(columns: List<String>, line: String, expectedColumnCount: Int): Boolean {
    if (!validateColumnCount(columns, expectedColumnCount)) {
        println("Warning: Invalid column count -> $line")
        return true
    }
    return false
}

private fun extractAndValidateMaxCapacity(columns: List<String>, line: String): Double {
    val maxCapacityKg = isPositiveDouble(columns[2])
    if (maxCapacityKg == DEFAULT_INVALID_DOUBLE) {
        println("Warning: Invalid maxCapacityKg in row: $line")
    }
    return maxCapacityKg
}

private fun extractAndValidateCostPerKm(columns: List<String>, line: String): Double {
    val costPerKm = isPositiveDouble(columns[3])
    if (costPerKm == DEFAULT_INVALID_DOUBLE) {
        println("Warning: Invalid costPerKm in row: $line")
    }
    return costPerKm
}

private fun createFleetObject(
    vehicleId: List<String>, currentHubId: String, maxCapacityKg: Double, costPerKm: Double
): FleetRaw {
    return FleetRaw(
        vehicleId = vehicleId, currentHubId = currentHubId, maxCapacityKg = maxCapacityKg, costPerKm = costPerKm
    )
}