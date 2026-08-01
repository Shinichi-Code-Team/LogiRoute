package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.dataholder.PackageRaw
import com.example.logiroute.data.processing.validation.*

private const val PACKAGE_ID_INDEX = 0
private const val PACKAGE_WEIGHT_INDEX = 1
private const val ORIGIN_HUB_ID_INDEX = 2
private const val DESTINATION_HUB_ID_INDEX = 3
private const val PACKAGE_PRIORITY_INDEX = 4

fun parsePackages(lines: List<String>): List<PackageRaw> {
    if (lines.isEmpty()) {
        return emptyList()
    }

    val expectedColumnCount =
        getExpectedColumnCount(lines.first())

    val packages = mutableListOf<PackageRaw>()

    for (line in skipHeader(lines)) {
        if (line.isBlank()) {
            continue
        }

        val columns = extractCleanColumns(line)

        if (!isValidPackageRaw(
                columns = columns,
                expectedColumnCount = expectedColumnCount,
                originalLine = line
            )
        ) {
            continue
        }

        packages.add(
            PackageRaw(
                id = columns[PACKAGE_ID_INDEX],
                weight = parsePositiveDoubleOrInvalid(columns[PACKAGE_WEIGHT_INDEX]),
                originHubId = columns[ORIGIN_HUB_ID_INDEX],
                destinationHubId = columns[DESTINATION_HUB_ID_INDEX],
                priority = parsePriority(columns[PACKAGE_PRIORITY_INDEX])
            )
        )
    }

    return packages
}

private fun isValidPackageRaw(
    columns: List<String>,
    expectedColumnCount: Int,
    originalLine: String
): Boolean {
    if (!hasExpectedColumnCount(columns, expectedColumnCount)) {
    //    println("Warning: Invalid column count -> $originalLine")
        return false
    }

    if (!hasRequiredPackageFields(columns)) {
        println(
          //  "Warning: Missing package ID or destination hub ID -> $originalLine"
        )
        return false
    }

    if (!isValidPackageWeight(columns[PACKAGE_WEIGHT_INDEX])) {
     //   println("Warning: Invalid package weight -> $originalLine")
        return false
    }

    return true
}

private fun hasRequiredPackageFields(
    columns: List<String>
): Boolean {
    val packageId = columns[PACKAGE_ID_INDEX]
    val destinationHubId = columns[DESTINATION_HUB_ID_INDEX]

    return isNotBlank(packageId) &&
            isNotBlank(destinationHubId)
}

private fun isValidPackageWeight(weight: String): Boolean {
    return parsePositiveDoubleOrInvalid(weight) != INVALID_DOUBLE_VALUE
}

