package com.example.logiroute.domain.model

data class FeatureBounds(
    val minLat: Double,
    val maxLat: Double,
    val minLon: Double,
    val maxLon: Double,
    val minWeight: Double,
    val maxWeight: Double
)