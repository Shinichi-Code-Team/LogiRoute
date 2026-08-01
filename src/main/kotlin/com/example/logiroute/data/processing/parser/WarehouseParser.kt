package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.dataholder.WarehouseRaw
import com.example.logiroute.data.processing.validation.*
import com.example.logiroute.data.processing.validation.INVALID_DOUBLE_VALUE

private const val WAREHOUSE_ID_INDEX = 0
private const val WAREHOUSE_NAME_INDEX = 1
private const val WAREHOUSE_REGIONAL_ZONE_INDEX = 2
private const val WAREHOUSE_LATITUDE_INDEX = 3
private const val WAREHOUSE_LONGITUDE_INDEX = 4
private const val MIN_LATITUDE = -90.0
private const val MAX_LATITUDE = 90.0
private const val MIN_LONGITUDE = -180.0
private const val MAX_LONGITUDE = 180.0


fun parseWarehouses(lines: List<String>): List<WarehouseRaw> {
    if (lines.isEmpty()) {
        return emptyList()
    }

    val expectedColumnCount =
        getExpectedColumnCount(lines.first())

    val warehouses = mutableListOf<WarehouseRaw>()

    for (line in skipHeader(lines)) {
        if (line.isBlank()) {
            continue
        }

        val columns = extractCleanColumns(line)

        if (!isValidWarehouseRaw(
                columns = columns,
                expectedColumnCount = expectedColumnCount,
                originalLine = line
            )
        ) {
            continue
        }

        warehouses.add(
            WarehouseRaw(
                id = columns[WAREHOUSE_ID_INDEX],
                name = columns[WAREHOUSE_NAME_INDEX],
                regionalZone = columns[WAREHOUSE_REGIONAL_ZONE_INDEX],
                latitude = parseCoordinateOrInvalid(
                    columns[WAREHOUSE_LATITUDE_INDEX],
                    MIN_LATITUDE,
                    MAX_LATITUDE
                ),
                longitude = parseCoordinateOrInvalid(
                    columns[WAREHOUSE_LONGITUDE_INDEX],
                    MIN_LONGITUDE,
                    MAX_LONGITUDE
                )
            )
        )
    }

    return warehouses
}

private fun isValidWarehouseRaw(
    columns: List<String>,
    expectedColumnCount: Int,
    originalLine: String
): Boolean {
    if (!hasExpectedColumnCount(columns, expectedColumnCount)) {
       // println("Warning: Invalid column count -> $originalLine")
        return false
    }

    if (!hasRequiredWarehouseFields(columns)) {
        println(
          //  "Warning: Missing warehouse ID, name or regional zone -> $originalLine"
        )
        return false
    }

    if (!isValidWarehouseLatitude(columns[WAREHOUSE_LATITUDE_INDEX])) {
      //  println("Warning: Invalid latitude -> $originalLine")
        return false
    }

    if (!isValidWarehouseLongitude(columns[WAREHOUSE_LONGITUDE_INDEX])) {
       // println("Warning: Invalid longitude -> $originalLine")
        return false
    }

    return true
}

private fun hasRequiredWarehouseFields(
    columns: List<String>
): Boolean {
    val id = columns[WAREHOUSE_ID_INDEX]
    val name = columns[WAREHOUSE_NAME_INDEX]
    val regionalZone = columns[WAREHOUSE_REGIONAL_ZONE_INDEX]

    return isNotBlank(id) &&
            isNotBlank(name) &&
            isNotBlank(regionalZone)
}

private fun isValidWarehouseLatitude(latitude: String): Boolean {
    return parseCoordinateOrInvalid(latitude, MIN_LATITUDE, MAX_LATITUDE) != INVALID_DOUBLE_VALUE
}

private fun isValidWarehouseLongitude(longitude: String): Boolean {
    return parseCoordinateOrInvalid(longitude, MIN_LONGITUDE, MAX_LONGITUDE) != INVALID_DOUBLE_VALUE
}