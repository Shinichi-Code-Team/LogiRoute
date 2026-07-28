package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.dataholder.PackageRaw
import com.example.logiroute.data.dataholder.PriorityRaw
import com.example.logiroute.data.processing.validation.*

fun parsePackages(lines: List<String>): List<PackageRaw> {
    if (lines.isEmpty()) {
        return emptyList()
    }

    val expectedColumnCount = getExpectedColumnCount(lines.first())
    val dataLines = skipHeader(lines)
    val packages = mutableListOf<PackageRaw>()

    for (line in dataLines) {
        if (line.isBlank()) {
            continue
        }
        val packageRaw = parseRawPackage(
            line = line,
            expectedColumnCount = expectedColumnCount
        )
        if (packageRaw != null) {
            packages.add(packageRaw)
        }
    }
    return packages
}

private fun parseRawPackage(
    line: String,
    expectedColumnCount: Int
): PackageRaw? {
    val columns = splitAndTrim(line)

    if (!hasValidPackageColumns(columns, expectedColumnCount, line)) {
        return null
    }

    val id = columns[0]
    val destinationHubId = columns[2]

    if (!hasPackageIds(id, destinationHubId)) {
        println(
            "Warning: Missing package ID or destination hub ID -> $line"
        )
        return null
    }

    val weight = parseWeight(
        weightText = columns[1],
        line = line
    )

    if (weight == DEFAULT_INVALID_DOUBLE) {
        return null
    }

    val priority = parsePriority(columns[3])

    return createPackageRaw(
        id = id,
        weight = weight,
        destinationHubId = destinationHubId,
        priority = priority
    )
}

private fun hasValidPackageColumns(
    columns: List<String>,
    expectedColumnCount: Int,
    line: String
): Boolean {
    val isValid = validateColumnCount(
        columns,
        expectedColumnCount
    )

    if (!isValid) {
        println("Warning: Invalid column count -> $line")
    }

    return isValid
}

private fun hasPackageIds(
    id: String,
    destinationHubId: String
): Boolean {
    return isNotBlank(id) &&
            isNotBlank(destinationHubId)
}

private fun parseWeight(
    weightText: String,
    line: String
): Double {
    val weight = isPositiveDouble(weightText)

    if (weight == DEFAULT_INVALID_DOUBLE) {
        println("Warning: Invalid weight -> $line")
    }
    return weight
}

private fun createPackageRaw(
    id: String,
    weight: Double,
    destinationHubId: String,
    priority: PriorityRaw
): PackageRaw {
    return PackageRaw(
        id = id,
        weight = weight,
        destinationHubId = destinationHubId,
        priority = priority
    )
}