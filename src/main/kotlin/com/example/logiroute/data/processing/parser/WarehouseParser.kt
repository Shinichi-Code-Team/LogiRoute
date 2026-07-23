package com.example.logiroute.data.processing.parser

import com.example.logiroute.data.dataholder.WarehouseRaw
import com.example.logiroute.data.processing.validation.*

fun warehouseParser(): MutableList<WarehouseRaw> {

    val lines = readCsvLines("warehouses.csv")

    if (lines.isEmpty()) {
        return mutableListOf()
    }
    val expectedColumnCount = getExpectedColumnCount(lines.first())

    val dataLines = skipHeader(lines)
    val warehouses = mutableListOf<WarehouseRaw>()


    for (line in dataLines) {

        if (!isNotBlank(line)) {
            continue
        }

        val columns = splitAndTrim(line)


        if (!validateColumnCount(columns, expectedColumnCount)) {
            println("Warning: Invalid column count -> $line")
            continue
        }

        val id = columns[0]
        val name = columns[1]
        val regionalZone = columns[2]

        if (!isNotBlank(id) || !isNotBlank(name) || !isNotBlank(regionalZone)) {
            println("Warning: Missing required warehouse fields -> $line")
            continue
        }

        warehouses.add(
            WarehouseRaw(
                id = id,
                name = name,
                regionalZone = regionalZone
            )
        )
    }

    return warehouses
}