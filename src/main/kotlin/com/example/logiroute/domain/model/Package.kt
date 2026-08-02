package com.example.logiroute.domain.model

import com.example.logiroute.data.dataholder.PriorityRaw

data class Package(
    val id: String,
    val weight: Double,
    val origin: Warehouse,
    val destination: Warehouse,
    val priority: PriorityRaw
)
{
    init {
        require(id.isNotBlank()) { "Package id must not be blank" }
        require(weight > 0) { "Package weight must be positive" }
    }
}
