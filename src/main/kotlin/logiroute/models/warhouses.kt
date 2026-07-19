package logiroute.models

import java.io.File

data class WareHouse(
    val id: String,
    val name: String,
    val regionalZone: String
)

val file = File("src/main/resources/warehouses.csv")
fun warehouseParser(file: File): List<WareHouse> {
    val warehousesList = mutableListOf<WareHouse>()
    if (!file.exists()) {
        return warehousesList
    }
    val filelines = file.readLines()
    for (i in 1 until filelines.size) {
        val line = filelines[i]
        if (line.isBlank()) continue

        val rows = line.split(",").map { it.trim() }

        if (rows.size < 3 || rows[0].isEmpty()) {
            println("Diagnostic Warning: Skipped warehouse row: $line")
            continue
        }
        val id = rows[0]
        val name = rows[1]
        val regionalZone = rows[2]
        warehousesList.add(WareHouse(id, name, regionalZone))
    }
    return warehousesList


}