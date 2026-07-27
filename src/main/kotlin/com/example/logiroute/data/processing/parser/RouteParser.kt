package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.dataholder.RouteRaw
import com.example.logiroute.data.processing.validation.*

fun parseRoutes(lines: List<String>): List<RouteRaw> {
    if (lines.isEmpty()) return emptyList()

    val expectedColumnCount = getExpectedColumnCount(lines.first())
    val dataLines = skipHeader(lines)
    val routes = mutableListOf<RouteRaw>()

    for (line in dataLines) {
        if (line.isBlank()) continue

        val route = parseRawRoute(line, expectedColumnCount)
        if (route != null) {
            routes.add(route)
        }
    }
    return routes
}

fun parseRawRoute(line: String, expectedColumnCount: Int): RouteRaw? {
    val columns = splitAndTrim(line)

    if (!hasValidRouteColumns(columns, expectedColumnCount, line)) {
        return null
    }

    val routeId = columns[0]
    val originHubId = columns[1]
    val destinationHubId = columns[2]

    if (!hasHubIds(routeId, originHubId, destinationHubId)) {
        println("Warning: Missing route ID, origin, or destination hub ID -> $line")
        return null
    }

    val distanceKm = parseDistance(columns[3], line) ?: return null
    val typicalDelayMin = parseDelay(columns[4], line) ?: return null

    return createRouteRaw(routeId, originHubId, destinationHubId, distanceKm, typicalDelayMin)
}

fun hasValidRouteColumns(columns: List<String>, expectedColumnCount: Int, line: String): Boolean {
    val isValid = validateColumnCount(columns, expectedColumnCount)
    if (!isValid) {
        println("Warning: Invalid column count -> $line")
    }
    return isValid
}

fun hasHubIds(routeId: String, originHubId: String, destinationHubId: String): Boolean {
    return isNotBlank(routeId) && isNotBlank(originHubId) && isNotBlank(destinationHubId)
}

fun parseDistance(distanceText: String, line: String): Double? {
    val distance = isPositiveDouble(distanceText)
    if (distance == DEFAULT_INVALID_DOUBLE) {
        println("Warning: Invalid distance value -> $line")
        return null
    }
    return distance
}

fun parseDelay(delayText: String, line: String): Int? {
    val delay = isPositiveInt(delayText)
    if (delay == DEFAULT_INVALID_INT) {
        println("Warning: Invalid delay value -> $line")
        return null
    }
    return delay
}

fun createRouteRaw(
    routeId: String,
    originHubId: String,
    destinationHubId: String,
    distanceKm: Double,
    typicalDelayMin: Int): RouteRaw {
    return RouteRaw(
        routeId = routeId,
        originHubId = originHubId,
        destinationHubId = destinationHubId,
        distanceKm = distanceKm,
        typicalDelayMin = typicalDelayMin
    )
}