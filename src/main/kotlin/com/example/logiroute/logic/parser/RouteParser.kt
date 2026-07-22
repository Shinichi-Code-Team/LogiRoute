package com.example.logiroute.logic.parser

import com.example.logiroute.logic.validation.*
import com.example.logiroute.dataholder.RouteRow

fun routeParser(): MutableList<RouteRow> {

    val lines = readCsvLines("routes.csv")

    if (lines.isEmpty()) {
        return mutableListOf()
    }

    val expectedColumnCount = getExpectedColumnCount(lines.first())

    val dataLines = skipHeader(lines)
    val routes = mutableListOf<RouteRow>()

    for (line in dataLines) {

        if (line.isBlank()) {
            continue
        }

        val columns = splitAndTrim(line)

        if (!validateColumnCount(columns, expectedColumnCount)) {
            println("Warning: Invalid column count -> $line")
            continue
        }

        val routeId = columns[0]
        val originHubId = columns[1]
        val destinationHubId = columns[2]

        if (!isNotBlank(routeId) || !isNotBlank(originHubId) || !isNotBlank(destinationHubId)) {
            println("Warning: Missing route ID, origin, or destination hub ID -> $line")
            continue
        }

        val distanceKm = isPositiveDouble(columns[3])
        if (distanceKm == DEFAULT_INVALID_DOUBLE) {
            println("Warning: Invalid distance value -> $line")
            continue
        }

        val typicalDelayMin = isPositiveInt(columns[4])
        if (typicalDelayMin == DEFAULT_INVALID_INT) {
            println("Warning: Invalid delay value -> $line")
            continue
        }

        routes.add(
            RouteRow(
                routeId = routeId,
                originHubId = originHubId,
                destinationHubId = destinationHubId,
                distanceKm = distanceKm,
                typicalDelayMin = typicalDelayMin
            )
        )
    }

    return routes
}