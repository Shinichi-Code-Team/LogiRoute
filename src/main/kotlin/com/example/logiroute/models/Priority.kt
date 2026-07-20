package com.example.logiroute.models

enum class Priority(val rank: Int) {
    LOW(1),
    STANDARD(2),
    URGENT(3)
}

fun parsePriority(value: String): Priority {
    return when (value.uppercase()) {
        "URGENT" -> Priority.URGENT
        "STANDARD" -> Priority.STANDARD
        else -> Priority.LOW


    }
}
