import java.io.File

private val source = File("src/main/resources")
private val output = File("developer-tools/supabase-migration/output")
private val report = mutableListOf<String>()

fun main() {
    output.mkdirs()

    val warehouses = cleanWarehouses()
    val warehouseIds = warehouses.map { it[0] }.toSet()

    cleanRoutes(warehouseIds)
    cleanPackages(warehouseIds)
    cleanVehicles(warehouseIds)

    File(output, "warehouses.csv").writeCsv(
        "id,name,regional_zone,latitude,longitude",
        warehouses
    )

    File(output, "migration-report.txt")
        .writeText(report.joinToString("\n"))

    println("Migration finished")
    println("Warehouses: ${warehouses.size}")
    println("Report: ${report.size} changes")
}

private fun cleanWarehouses(): List<List<String>> =
    File(source, "warehouses.csv")
        .readCsv()
        .mapIndexedNotNull { index, row ->
            val line = index + 2

            if (row.size != 5) {
                drop("warehouses", line, "invalid columns")
                return@mapIndexedNotNull null
            }

            val name = row[1].trim()

            val originalId = row[0].trim()

            val id = originalId
                .uppercase()
                .ifBlank {
                    warehouseIdFrom(name)?.also {
                        repair(
                            "warehouses",
                            line,
                            "missing id -> $it"
                        )
                    }
                }

            if (id.isNullOrBlank()) {
                drop("warehouses", line, "missing id")
                return@mapIndexedNotNull null
            }

            if (name.isBlank()) {
                drop("warehouses", line, "missing name")
                return@mapIndexedNotNull null
            }

            if (
                originalId.isNotBlank() &&
                originalId != id
            ) {
                repair(
                    "warehouses",
                    line,
                    "$originalId -> $id"
                )
            }

            val latitude = nullableNumber(row[3])
            val longitude = nullableNumber(row[4])

            if (!missing(row[3]) && latitude == null) {
                drop("warehouses", line, "invalid latitude")
                return@mapIndexedNotNull null
            }

            if (!missing(row[4]) && longitude == null) {
                drop("warehouses", line, "invalid longitude")
                return@mapIndexedNotNull null
            }

            if (latitude != null && latitude !in -90.0..90.0) {
                drop("warehouses", line, "latitude out of range")
                return@mapIndexedNotNull null
            }

            if (longitude != null && longitude !in -180.0..180.0) {
                drop("warehouses", line, "longitude out of range")
                return@mapIndexedNotNull null
            }

            if (missing(row[3])) {
                repair("warehouses", line, "latitude -> NULL")
            }

            if (missing(row[4])) {
                repair("warehouses", line, "longitude -> NULL")
            }

            listOf(
                id,
                name,
                row[2].trim(),
                latitude?.toString().orEmpty(),
                longitude?.toString().orEmpty()
            )
        }
        .removeDuplicates("warehouses")

private fun cleanRoutes(
    warehouseIds: Set<String>
) {
    val rows = File(source, "routes.csv")
        .readCsv()
        .mapIndexedNotNull { index, raw ->
            val line = index + 2
            val row = raw.removeTrailingEmpty()

            if (row.size != 5) {
                drop("routes", line, "invalid columns")
                return@mapIndexedNotNull null
            }

            val id = row[0].cleanId()
            val origin = row[1].cleanId()
            val destination = row[2].cleanId()
            val distance = row[3].trim().toDoubleOrNull()
            val delay = row[4].trim().toIntOrNull()

            val reason = when {
                id.isBlank() -> "missing id"
                origin.isBlank() -> "missing origin"
                destination.isBlank() -> "missing destination"
                distance == null || distance <= 0 -> "invalid distance"
                delay == null || delay < 0 -> "invalid delay"
                origin !in warehouseIds -> "origin $origin not found"
                destination !in warehouseIds -> "destination $destination not found"
                else -> null
            }

            if (reason != null) {
                drop("routes", line, reason)
                null
            } else {
                listOf(
                    id,
                    origin,
                    destination,
                    distance.toString(),
                    delay.toString()
                )
            }
        }
        .removeDuplicates("routes")

    File(output, "routes.csv").writeCsv(
        "id,origin_hub_id,destination_hub_id,distance_km,typical_delay_min",
        rows
    )

    println("Routes: ${rows.size}")
}

private fun cleanPackages(
    warehouseIds: Set<String>
) {
    val rows = File(source, "packages.csv")
        .readCsv()
        .mapIndexedNotNull { index, raw ->
            val line = index + 2
            val row = raw.removeTrailingEmpty()

            if (row.size != 5) {
                drop("packages", line, "invalid columns")
                return@mapIndexedNotNull null
            }

            val id = row[0].cleanId()
            val weight = row[1].trim().toDoubleOrNull()
            val origin = row[2].cleanId()
            val destination = row[3].cleanId()
            val priority = row[4].trim().uppercase()

            val reason = when {
                id.isBlank() -> "missing id"
                weight == null || weight <= 0 -> "invalid weight"
                origin.isBlank() -> "missing origin"
                destination.isBlank() -> "missing destination"
                priority !in setOf("LOW", "STANDARD", "URGENT") ->
                    "invalid priority"
                origin !in warehouseIds -> "origin $origin not found"
                destination !in warehouseIds ->
                    "destination $destination not found"
                else -> null
            }

            if (reason != null) {
                drop("packages", line, reason)
                null
            } else {
                listOf(
                    id,
                    weight.toString(),
                    origin,
                    destination,
                    priority
                )
            }
        }
        .removeDuplicates("packages")

    File(output, "packages.csv").writeCsv(
        "id,weight,origin_hub_id,destination_hub_id,priority",
        rows
    )

    println("Packages: ${rows.size}")
}

private fun cleanVehicles(
    warehouseIds: Set<String>
) {
    val rows = File(source, "fleet.csv")
        .readCsv()
        .mapIndexedNotNull { index, raw ->
            val line = index + 2
            val row = raw.removeTrailingEmpty()

            if (row.size != 4) {
                drop("fleet", line, "invalid columns")
                return@mapIndexedNotNull null
            }

            val id = row[0].cleanId()
            val hub = row[1].cleanId()
            val capacity = row[2].trim().toDoubleOrNull()
            val cost = row[3].trim().toDoubleOrNull()

            val reason = when {
                id.isBlank() -> "missing vehicle id"
                hub.isBlank() -> "missing warehouse"
                capacity == null || capacity <= 0 -> "invalid capacity"
                cost == null || cost < 0 -> "invalid cost"
                hub !in warehouseIds -> "warehouse $hub not found"
                else -> null
            }

            if (reason != null) {
                drop("fleet", line, reason)
                null
            } else {
                listOf(
                    id,
                    hub,
                    capacity.toString(),
                    cost.toString()
                )
            }
        }
        .removeDuplicates("vehicles")

    File(output, "vehicles.csv").writeCsv(
        "id,current_hub_id,max_capacity_kg,cost_per_km",
        rows
    )

    println("Vehicles: ${rows.size}")
}

private fun warehouseIdFrom(name: String): String? =
    Regex("""(?i)^Hub-(\d+)$""")
        .matchEntire(name.trim())
        ?.groupValues
        ?.get(1)
        ?.toIntOrNull()
        ?.let {
            "WH-${it.toString().padStart(3, '0')}"
        }

private fun String.cleanId(): String =
    trim().uppercase()

private fun missing(value: String): Boolean =
    value.trim().let {
        it.isBlank() ||
                it.equals("N/A", true) ||
                it.equals("NULL", true)
    }

private fun nullableNumber(value: String): Double? =
    if (missing(value)) null
    else value.trim().toDoubleOrNull()

private fun List<String>.removeTrailingEmpty(): List<String> =
    dropLastWhile { it.isBlank() }

private fun List<List<String>>.removeDuplicates(
    file: String
): List<List<String>> =
    groupBy { it[0] }
        .flatMap { (id, rows) ->
            if (rows.size > 1) {
                report +=
                    "DROPPED | $file | duplicate $id | kept first"
            }

            rows.take(1)
        }

private fun repair(
    file: String,
    line: Int,
    reason: String
) {
    report += "REPAIRED | $file | row $line | $reason"
}

private fun drop(
    file: String,
    line: Int,
    reason: String
) {
    report += "DROPPED | $file | row $line | $reason"
}

private fun File.readCsv(): List<List<String>> =
    readLines()
        .drop(1)
        .filter(String::isNotBlank)
        .map(::parseCsv)

private fun parseCsv(line: String): List<String> {
    val result = mutableListOf<String>()
    val current = StringBuilder()
    var quoted = false
    var i = 0

    while (i < line.length) {
        when {
            line[i] == '"' &&
                    quoted &&
                    i + 1 < line.length &&
                    line[i + 1] == '"' -> {
                current.append('"')
                i++
            }

            line[i] == '"' ->
                quoted = !quoted

            line[i] == ',' && !quoted -> {
                result += current.toString()
                current.clear()
            }

            else ->
                current.append(line[i])
        }

        i++
    }

    return result + current.toString()
}

private fun File.writeCsv(
    header: String,
    rows: List<List<String>>
) {
    writeText(
        buildList {
            add(header)

            addAll(
                rows.map { row ->
                    row.joinToString(",") { escape(it) }
                }
            )
        }.joinToString("\n")
    )
}

private fun escape(value: String): String =
    if (
        ',' in value ||
        '"' in value ||
        '\n' in value
    ) {
        "\"${value.replace("\"", "\"\"")}\""
    } else {
        value
    }