package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.dataholder.FleetRaw
import com.example.logiroute.data.processing.validation.*

fun parseFleets(lines: List<String>): List<FleetRaw> {
    if (lines.isEmpty()) return emptyList()

    val expectedColumnCount = getExpectedColumnCount(lines.first())
    val dataLines = skipHeader(lines)
    val fleetList = mutableListOf<FleetRaw>()

    for (line in dataLines) {
        if (line.isBlank()) continue

        val fleet = parseRawFleet(line, expectedColumnCount)
        if (fleet != null) {
            fleetList.add(fleet)
        }
    }

    return fleetList
}

fun parseRawFleet(line: String, expectedColumnCount: Int): FleetRaw? {
    val columns = splitAndTrim(line)

    if (!hasValidFleetColumns(columns, expectedColumnCount, line)) {
        return null
    }

    val vehicleId = listOf(columns[0])
    val currentHubId = columns[1]

    val maxCapacityKg = parseFleetCapacity(columns[2], line) ?: return null
    val costPerKm = parseFleetCost(columns[3], line) ?: return null

    return createFleetRaw(vehicleId, currentHubId, maxCapacityKg, costPerKm)
}

fun hasValidFleetColumns(columns: List<String>, expectedColumnCount: Int, line: String): Boolean {
    val isValid = validateColumnCount(columns, expectedColumnCount)
    if (!isValid) {
        println("Warning: Invalid column count -> $line")
    }
    return isValid
}

fun parseFleetCapacity(capacityText: String, line: String): Double? {
    val maxCapacityKg = isPositiveDouble(capacityText)
    if (maxCapacityKg == DEFAULT_INVALID_DOUBLE) {
        println("Warning: Invalid maxCapacityKg in row: $line")
        return null
    }
    return maxCapacityKg
}

fun parseFleetCost(costText: String, line: String): Double? {
    val costPerKm = isPositiveDouble(costText)
    if (costPerKm == DEFAULT_INVALID_DOUBLE) {
        println("Warning: Invalid costPerKm in row: $line")
        return null
    }
    return costPerKm
}

fun createFleetRaw(
    vehicleId: List<String>,
    currentHubId: String,
    maxCapacityKg: Double,
    costPerKm: Double
): FleetRaw {
    return FleetRaw(
        vehicleId = vehicleId,
        currentHubId = currentHubId,
        maxCapacityKg = maxCapacityKg,
        costPerKm = costPerKm
    )
}