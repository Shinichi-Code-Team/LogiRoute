package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.dataholder.RouteRaw
import com.example.logiroute.data.processing.validation.*

private const val ROUTE_ID_INDEX = 0
private const val ORIGIN_HUB_ID_INDEX = 1
private const val DESTINATION_HUB_ID_INDEX = 2
private const val DISTANCE_KM_INDEX = 3
private const val TYPICAL_DELAY_MIN_INDEX = 4

fun parseRoutes(lines: List<String>): List<RouteRaw> {
    if (lines.isEmpty()) return emptyList()

    val expectedColumnCount = getExpectedColumnCount(lines.first())
    val routes = mutableListOf<RouteRaw>()

    for (line in skipHeader(lines)) {
        if (line.isBlank()) continue

        val columns = extractCleanColumns(line)

        if (!isValidRouteRaw(columns, expectedColumnCount, line)) {
            continue
        }

        routes.add(
            RouteRaw(
                id = columns[ROUTE_ID_INDEX],
                originHubId = columns[ORIGIN_HUB_ID_INDEX],
                destinationHubId = columns[DESTINATION_HUB_ID_INDEX],
                distanceKm = parsePositiveDoubleOrInvalid(columns[DISTANCE_KM_INDEX]),
                typicalDelayMin = parseNonNegativeIntOrInvalid(columns[TYPICAL_DELAY_MIN_INDEX])
            )
        )
    }
    return routes
}

private fun isValidRouteRaw(
    columns: List<String>,
    expectedColumnCount: Int,
    originalLine: String
): Boolean {
    if (!hasExpectedColumnCount(columns, expectedColumnCount)) {
        println("Warning: Invalid column count -> $originalLine")
        return false
    }

    if (!hasRequiredRouteFields(columns)) {
        println("Warning: Missing route ID, origin, or destination hub ID -> $originalLine")
        return false
    }

    if (!isValidRouteDistance(columns[DISTANCE_KM_INDEX])) {
        println("Warning: Invalid distance value -> $originalLine")
        return false
    }

    if (!isValidRouteDelay(columns[TYPICAL_DELAY_MIN_INDEX])) {
        println("Warning: Invalid delay value -> $originalLine")
        return false
    }

    return true
}

private fun hasRequiredRouteFields(columns: List<String>): Boolean {
    val routeId = columns[ROUTE_ID_INDEX]
    val originHubId = columns[ORIGIN_HUB_ID_INDEX]
    val destinationHubId = columns[DESTINATION_HUB_ID_INDEX]

    return isNotBlank(routeId) && isNotBlank(originHubId) && isNotBlank(destinationHubId)
}

private fun isValidRouteDistance(distanceText: String): Boolean {
    return parsePositiveDoubleOrInvalid(distanceText) != INVALID_DOUBLE_VALUE
}

private fun isValidRouteDelay(delayText: String): Boolean {
    return parseNonNegativeIntOrInvalid(delayText) != INVALID_INT_VALUE
}