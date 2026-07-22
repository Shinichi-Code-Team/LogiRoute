package com.example.logiroute.logic.parser

import com.example.logiroute.dataholder.FleetRow
import com.example.logiroute.logic.validation.*


fun parseFleetCsv(fileName: String): List<FleetRow> {
    val lines = readCsvLines(fileName)
    if (lines.isEmpty()) return emptyList()

    val expectedColumnCount = getExpectedColumnCount(lines.first())

    val dataLines = skipHeader(lines)
    val fleetList = mutableListOf<FleetRow>()

    for (line in dataLines) {
        if (!isNotBlank(line)) continue

        val columns = splitAndTrim(line)

        if (!validateColumnCount(columns, expectedColumnCount)) {
            println("Warning: Invalid column count -> $line")
            continue
        }

        val vehicleId = listOf(columns[0])
        val currentHubId = columns[1]

        val maxCapacityKg = isPositiveDouble(columns[2])
        if (maxCapacityKg == DEFAULT_INVALID_DOUBLE) {
            println("Warning: Invalid maxCapacityKg in row: $line")
            continue
        }

        val costPerKm = isPositiveDouble(columns[3])
        if (costPerKm == DEFAULT_INVALID_DOUBLE) {
            println("Warning: Invalid costPerKm in row: $line")
            continue
        }

        val fleetRow = FleetRow(
            vehicleId = vehicleId,
            currentHubId = currentHubId,
            maxCapacityKg = maxCapacityKg,
            costPerKm = costPerKm
        )

        fleetList.add(fleetRow)
    }

    return fleetList
}