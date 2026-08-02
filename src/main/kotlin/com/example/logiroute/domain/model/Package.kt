package com.example.logiroute.domain.model

data class Package(
    val id: String,
    val weight: Double,
    val origin: Warehouse,
    val destination: Warehouse,
    val priority: Priority
) {
    fun compareWeight(otherPackage: Package): Int {
        return weight.compareTo(otherPackage.weight)
    }
}
