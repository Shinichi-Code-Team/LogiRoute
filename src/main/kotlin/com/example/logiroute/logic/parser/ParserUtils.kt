import java.io.File


fun readCsvLines(fileName: String): List<String> {
    val file = File("src/main/resources/$fileName")
    if (!file.exists()) return emptyList()

    return file.readLines()
}

fun getExpectedColumnCount(header: String): Int {
    return header.split(",").size
}

fun skipHeader(lines: List<String>): List<String> {
    return if (lines.size > 1) lines.drop(1) else emptyList()
}


fun splitAndTrim(line: String): List<String> {
    return line.split(",").map { it.trim() }
}


