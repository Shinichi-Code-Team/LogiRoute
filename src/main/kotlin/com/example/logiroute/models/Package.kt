package com.example.logiroute.models

import java.io.File

/**
 * Represents a package in the logistics system.
 *
 * @property id unique identifier of the package.
 * @property weight package weight in kilograms.
 * @property destinationHubId identifier of the destination warehouse.
 * @property priority delivery priority of the package.
 */
data class Package(
    val id: String,
    val weight: Double,
    val destinationHubId: String,
    val priority: Priority


)

/**
 * Reads all lines from the packages CSV file.
 *
 * @return a list containing all file lines,
 * or an empty list if the file does not exist.
 */
fun readPackageLines(): List<String> {
    val file = File("src/main/resources/packages.csv")

    if (!file.exists()) {
        println("File not found")
        return emptyList()
    }

    return file.readLines()
}

/**
 * Validates and converts one CSV row into a Package object.
 *
 * The function checks:
 * - Blank rows.
 * - The number of columns.
 * - Missing package identifiers.
 * - Missing destination hub identifiers.
 * - Invalid or non-positive package weights.
 *
 * @param line the CSV row to parse.
 * @param lineNumber the actual row number in the CSV file.
 * @return a valid Package object, or null if the row is invalid.
 */
fun parsePackageRow(line: String, lineNumber: Int): Package? {
    if (line.isBlank()) {
        return null
    }

    val listRow = line
        .split(",")
        .map { it.trim() }

    if (listRow.size != 4) {
        println("Invalid row at line $lineNumber")
        return null
    }

    val id = listRow[0].uppercase()
    val weight = listRow[1].toDoubleOrNull()
    val destinationHubId = listRow[2].uppercase()
    val priority = parsePriority(listRow[3])

    if (id.isBlank() || destinationHubId.isBlank()) {
        println("Missing data at line $lineNumber")
        return null
    }

    if (weight == null || weight <= 0) {
        println("Invalid weight at line $lineNumber")
        return null
    }

    return Package(
        id = id,
        weight = weight,
        destinationHubId = destinationHubId,
        priority = priority
    )
}

/**
 * Parses the packages CSV file and collects all valid packages.
 *
 * The first CSV row is skipped because it contains the header.
 * Invalid rows are ignored without stopping the program.
 *
 * @return a mutable list containing all successfully parsed packages.
 */
fun parsePackages(): MutableList<Package> {
    val packages = mutableListOf<Package>()
    val lines = readPackageLines()
    if (lines.isEmpty()) {
        return packages
    }
    for (index in 1 until lines.size) {
        val packageItem = parsePackageRow(
            line = lines[index],
            lineNumber = index + 1
        )

        if (packageItem != null) {
            packages.add(packageItem)
        }
    }

    return packages
}