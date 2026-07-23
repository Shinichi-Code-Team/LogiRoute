package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.processing.validation.*
import com.example.logiroute.data.dataholder.RouteRaw

fun routeParser(): MutableList<RouteRaw> {

    val lines = readCsvLines("routes.csv")

    if (lines.isEmpty()) {
        return mutableListOf()
    }

    val expectedColumnCount = getExpectedColumnCount(lines.first())

    val dataLines = skipHeader(lines)
    val routes = mutableListOf<RouteRaw>()

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
            RouteRaw(
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