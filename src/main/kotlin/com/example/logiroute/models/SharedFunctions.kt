package org.example.com.example.logiroute.models
import java.io.File

fun readCsvLines(fileName: String): List<String> {
    val file = File("src/main/resources/$fileName")
    return if (file.exists()) file.readLines()
                 else emptyList()

}

fun isValidRow(line: String, expectedCount: Int): Boolean {
    if (line.isBlank()) return false

    val parts = line.split(",")
    return parts.size == expectedCount
}

fun splitAndTrim(line: String): List<String> {
    return line.split(",").map { it.trim() }
}

fun String.toDoubleSafe(): Double {
    return this.toDoubleOrNull() ?: -1.0
}