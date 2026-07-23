package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.dataholder.PackageRaw
import com.example.logiroute.data.processing.validation.*

fun packageParser(): MutableList<PackageRaw> {

    val lines = readCsvLines("packages.csv")

    if (lines.isEmpty()) {
        return mutableListOf()
    }

    val expectedColumnCount = getExpectedColumnCount(lines.first())

    val dataLines = skipHeader(lines)
    val packages = mutableListOf<PackageRaw>()

    for (line in dataLines) {

        if (line.isBlank()) {
            continue
        }

        val columns = splitAndTrim(line)

        if (!validateColumnCount(columns, expectedColumnCount)) {
            println("Warning: Invalid column count -> $line")
            continue
        }

        val id = columns[0]
        val destinationHubId = columns[2]

        if (!isNotBlank(id) || !isNotBlank(destinationHubId)) {
            println("Warning: Missing package ID or destination hub ID -> $line")
            continue
        }

        val weight = isPositiveDouble(columns[1])

        if (weight == DEFAULT_INVALID_DOUBLE) {
            println("Warning: Invalid weight -> $line")
            continue
        }

        val priority = parsePriority(columns[3])

        packages.add(
            PackageRaw(
                id = id,
                weight = weight,
                destinationHubId = destinationHubId,
                priority = priority
            )
        )
    }

    return packages
}