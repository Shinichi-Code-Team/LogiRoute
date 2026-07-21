import com.example.logiroute.dataholder.Priority
import java.io.File


fun readCsvLines(fileName: String): List<String> {
    val file = File("src/main/resources/$fileName")
    if (!file.exists()) return emptyList()

    val lines = file.readLines()
    return if (lines.isNotEmpty()) lines.drop(1) else emptyList()
}


fun splitAndTrim(line: String): List<String> {
    return line.split(",").map { it.trim() }
}

fun parsePriority(value: String): Priority {
    return when (value.uppercase()) {
        "URGENT" -> Priority.URGENT
        "STANDARD" -> Priority.STANDARD
        else -> Priority.LOW


    }
}

