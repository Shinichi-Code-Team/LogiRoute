package com.example.logiroute.logic.parser

import com.example.logiroute.dataholder.WarehouseRow
import com.example.logiroute.logic.validation.*

fun warehouseParser(): MutableList<WarehouseRow> {

    val lines = readCsvLines("warehouses.csv")

    if (lines.isEmpty()) {
        return mutableListOf()
    }
    val expectedColumnCount = getExpectedColumnCount(lines.first())

    val warehouses = mutableListOf<WarehouseRow>()


    for (line in lines.drop(1)) {

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
            WarehouseRow(
                id = id,
                name = name,
                regionalZone = regionalZone
            )
        )
    }

    return warehouses
}