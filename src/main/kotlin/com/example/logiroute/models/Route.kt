package com.example.logiroute.models

import java.io.File

/**
 * Domain model representing a logistics shipping route.
 *
 * @property routeId The unique identifier string for this specific route.
 * @property originHubId The starting warehouse or hub identifier.
 * @property destinationHubId The target warehouse or hub identifier for delivery.
 * @property distance The transit distance measured in kilometers.
 */
data class Route(
    val routeId: String,
    val originHubId: String,
    val destinationHubId: String,
    val distance: Double
)

/**
 * Accesses the storage directory to extract all raw string rows from routes.csv.
 *
 * @param path The relative file path to the routes CSV resource.
 * @return A list containing all lines of text from the file, or an empty list if not found.
 */
fun readRouteCsvLines(path: String = "src/main/resources/routes.csv"): List<String> {
    val csvFile = File(path)
    if (!csvFile.exists()) {
        println("Diagnostic Warning: File not found at $path")
        return emptyList()
    }
    return csvFile.readLines()
}

/**
 * Inspects, cleans, and transforms a single CSV line into a structured Route instance.
 *
 * Performs checks on:
 * - Empty or whitespace-only lines.
 * - Accurate column structure count.
 * - Existence of necessary identifiers.
 * - Numeric integrity of distance metrics (falls back to -1.0 if malformed).
 *
 * @param line The individual comma-separated string row.
 * @param lineNum The sequential index position of the row within the CSV document.
 * @return A fully validated [Route] object, or null if the row violates any parsing constraint.
 */
fun parseSingleRouteRow(line: String, lineNum: Int): Route? {
    if (line.isBlank()) return null

    val tokens = line.split(",").map { it.trim() }

    if (tokens.size != 4) {
        println("Diagnostic Warning: Line $lineNum skipped due to invalid column count (${tokens.size}/4)")
        return null
    }

    val id = tokens[0].uppercase()
    val origin = tokens[1].uppercase()
    val destination = tokens[2].uppercase()

    val parsedDistance = tokens[3].toDoubleOrNull() ?: -1.0

    if (id.isBlank() || origin.isBlank() || destination.isBlank()) {
        println("Diagnostic Warning: Line $lineNum skipped due to missing identifiers")
        return null
    }

    if (parsedDistance <= 0) {
        println("Diagnostic Warning: Line $lineNum has invalid numeric distance ($parsedDistance)")
        return null
    }

    return Route(
        routeId = id,
        originHubId = origin,
        destinationHubId = destination,
        distance = parsedDistance
    )
}

/**
 * Processes the complete routes dataset, bypassing the initial header row.
 *
 * Iterates through all available rows and filters out any records that do not pass validation.
 *
 * @return A mutable list containing all successfully structured [Route] records.
 */
fun parseAllRoutes(): MutableList<Route> {
    val routeList = mutableListOf<Route>()
    val lines = readRouteCsvLines()

    if (lines.isEmpty()) return routeList

    for (i in 1 until lines.size) {
        val parsedRoute = parseSingleRouteRow(lines[i], lineNum = i + 1)
        if (parsedRoute != null) {
            routeList.add(parsedRoute)
        }
    }

    return routeList
}