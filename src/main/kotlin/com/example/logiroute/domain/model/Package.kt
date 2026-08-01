package com.example.logiroute.domain.model

import com.example.logiroute.data.dataholder.PackageRaw
import com.example.logiroute.data.dataholder.PriorityRaw

data class Package(
    val id: String,
    val weight: Double,
    val origin: Warehouse,
    val destination: Warehouse,
    val priority: PriorityRaw
) {
    fun compareWeight(otherPackage: Package): Int {
        return weight.compareTo(otherPackage.weight)
    }
}
