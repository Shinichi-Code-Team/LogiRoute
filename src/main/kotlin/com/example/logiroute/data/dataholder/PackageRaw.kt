package com.example.logiroute.data.dataholder


data class PackageRaw(
    val id: String,
    val weight: Double,
    val destinationHubId: String,
    val priority: PriorityRaw
) {
    fun compareWeight(otherPackage: PackageRaw): Int {
        return weight.compareTo(otherPackage.weight)
    }
}



