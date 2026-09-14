package com.example.logiroute.data.csv.raw


data class PackageRaw(
    val id: String,
    val weight: Double,
    val originHubId: String,
    val destinationHubId: String,
    val priority: PriorityRaw
)
