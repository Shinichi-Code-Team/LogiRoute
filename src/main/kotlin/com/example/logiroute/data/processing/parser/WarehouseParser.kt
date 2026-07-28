package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.dataholder.WarehouseRaw
import com.example.logiroute.data.processing.validation.*

fun parseWarehouses(lines: List<String>): MutableList<WarehouseRaw> {

    if (lines.isEmpty()) return mutableListOf()

    val expectedColumnCount = getExpectedColumnCount(lines.first())
 
    val dataLines = skipHeader(lines)

    return dataLines
        .mapNotNull { parseWarehouseLine(it, expectedColumnCount) }.toMutableList()
}

private fun parseWarehouseLine(line: String, expectedColumnCount: Int): WarehouseRaw?{
    if (line.isBlank()) return null

    val columns = splitAndTrim(line)

    if (!validateColumnCount(columns, expectedColumnCount)) {
        println("Warning: Invalid column count -> $line")
        return null
    }
    if (!hasRequiredWarehouseFields(columns)) {
        println("Warning: Missing required warehouse fields -> $line")
        return null
    }

    return buildWarehouseRaw(columns)
}

private fun hasRequiredWarehouseFields(columns: List<String>): Boolean {
    return isNotBlank(columns[0]) && isNotBlank(columns[1]) && isNotBlank(columns[2])
}

private fun buildWarehouseRaw(columns: List<String>): WarehouseRaw {
    return WarehouseRaw(
        id = columns[0],
        name = columns[1],
        regionalZone = columns[2]
    )
}