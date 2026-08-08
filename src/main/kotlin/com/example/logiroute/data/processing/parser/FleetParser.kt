package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.dataholder.FleetRaw
import com.example.logiroute.data.processing.validation.*

private const val VEHICLE_ID_INDEX = 0
private const val CURRENT_HUB_ID_INDEX = 1
private const val MAX_CAPACITY_KG_INDEX = 2
private const val COST_PER_KM_INDEX = 3

fun parseFleets(lines: List<String>): List<FleetRaw> {
    if (lines.isEmpty()) return emptyList()

    val expectedColumnCount = getExpectedColumnCount(lines.first())
    val fleetList = mutableListOf<FleetRaw>()

    for (line in skipHeader(lines)) {
        if (line.isBlank()) continue

        val columns = extractCleanColumns(line)

        if (!isValidFleetRaw(columns, expectedColumnCount, line)) {
            continue
        }

        fleetList.add(
            FleetRaw(
                vehicleIds = listOf(columns[VEHICLE_ID_INDEX]),
                currentHubId = columns[CURRENT_HUB_ID_INDEX],
                maxCapacityKg = parsePositiveDoubleOrInvalid(columns[MAX_CAPACITY_KG_INDEX]),
                costPerKm = parsePositiveDoubleOrInvalid(columns[COST_PER_KM_INDEX])
            )
        )
    }
    return fleetList
}

private fun isValidFleetRaw(
    columns: List<String>,
    expectedColumnCount: Int,
    originalLine: String
): Boolean {
    if (!hasExpectedColumnCount(columns, expectedColumnCount)) {
        println("Warning: Invalid column count -> $originalLine")
        return false
    }

    if (!isValidFleetCapacity(columns[MAX_CAPACITY_KG_INDEX])) {
        println("Warning: Invalid maxCapacityKg in row: $originalLine")
        return false
    }

    if (!isValidFleetCost(columns[COST_PER_KM_INDEX])) {
        println("Warning: Invalid costPerKm in row: $originalLine")
        return false
    }
    return true
}

private fun isValidFleetCapacity(capacity: String): Boolean {
    return parsePositiveDoubleOrInvalid(capacity) != INVALID_DOUBLE_VALUE
}

private fun isValidFleetCost(cost: String): Boolean {
    return parsePositiveDoubleOrInvalid(cost) != INVALID_DOUBLE_VALUE
}