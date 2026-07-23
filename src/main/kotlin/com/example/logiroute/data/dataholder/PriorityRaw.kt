package com.example.logiroute.data.dataholder

enum class PriorityRaw {
    LOW,
    STANDARD,
    URGENT;

    fun comparePriority(otherPriority: PriorityRaw): Int {
        return compareTo(otherPriority)
    }
}




