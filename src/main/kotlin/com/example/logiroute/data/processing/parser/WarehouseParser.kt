package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.dataholder.WarehouseRaw
import com.example.logiroute.data.processing.validation.*

fun parseWarehouses(lines: List<String>): List<WarehouseRaw> {
    if (lines.isEmpty()) return emptyList()

    val expectedColumnCount = getExpectedColumnCount(lines.first())
    val dataLines = skipHeader(lines)
    val warehouses = mutableListOf<WarehouseRaw>()

    for (line in dataLines) {
        if (line.isBlank()) continue

        val warehouse = parseRawWarehouse(line, expectedColumnCount)
        if (warehouse != null) {
            warehouses.add(warehouse)
        }
    }

    return warehouses
}

fun parseRawWarehouse(line: String, expectedColumnCount: Int): WarehouseRaw? {
    val columns = extractCleanColumns(line)

    if (!hasValidWarehouseColumns(columns, expectedColumnCount, line)) {
        return null
    }

    val id = columns[0]
    val name = columns[1]
    val regionalZone = columns[2]

    if (!hasWarehouseFields(id, name, regionalZone)) {
        println("Warning: Missing required warehouse fields -> $line")
        return null
    }

    return createWarehouseRaw(id, name, regionalZone)
}

fun hasValidWarehouseColumns(columns: List<String>, expectedColumnCount: Int, line: String): Boolean {
    val isValid = hasExpectedColumnCount(columns, expectedColumnCount)
    if (!isValid) {
        println("Warning: Invalid column count -> $line")
    }
    return isValid
}

fun hasWarehouseFields(id: String, name: String, regionalZone: String): Boolean {
    return isNotBlank(id) && isNotBlank(name) && isNotBlank(regionalZone)
}

fun createWarehouseRaw(id: String, name: String, regionalZone: String): WarehouseRaw {
    return WarehouseRaw(
        id = id,
        name = name,
        regionalZone = regionalZone
    )
}