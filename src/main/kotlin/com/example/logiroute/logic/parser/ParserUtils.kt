import com.example.logiroute.dataholder.PriorityRow
import java.io.File

fun readCsvLines(fileName: String): List<String> {
    val file = File("src/main/resources/$fileName")

    return if (file.exists()) {
        file.readLines()
    } else {
        emptyList()
    }
}

fun splitAndTrim(line: String): List<String> {
    return line.split(",").map { it.trim() }
}

fun parsePriority(value: String): PriorityRow {
    return when (value.uppercase().trim()) {
        "URGENT" -> PriorityRow.URGENT
        "STANDARD" -> PriorityRow.STANDARD
        else -> PriorityRow.LOW
    }
}