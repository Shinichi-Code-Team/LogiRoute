import com.example.logiroute.dataholder.PackageRow
import com.example.logiroute.dataholder.PriorityRow
import com.example.logiroute.logic.validation.*
import com.example.logiroute.logic.parser.*

fun packageParser(): MutableList<PackageRow> {

    val lines = readCsvLines("packages.csv")

    if (lines.isEmpty()) {
        return mutableListOf()
    }

    val expectedColumnCount = getExpectedColumnCount(lines.first())

    val packages = mutableListOf<PackageRow>()

    for (line in lines.drop(1)) {

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
            PackageRow(
                id = id,
                weight = weight,
                destinationHubId = destinationHubId,
                priority = priority
            )
        )
    }

    return packages
}