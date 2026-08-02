package com.example.logiroute.domain.model

enum class Priority {
    LOW,
    STANDARD,
    URGENT;

    fun comparePriority(otherPriority: Priority): Int {
        return compareTo(otherPriority)
    }
}