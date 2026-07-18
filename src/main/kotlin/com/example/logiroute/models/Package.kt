package com.example.logiroute.models

data class Package(
    val id: String,
    val weight: Double,
    val destinationHubId: String,
    val priority: Priority
)