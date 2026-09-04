package com.example.logiroute.domain.model.result

data class RouteEvaluationResult(
    val totalDistanceKm: Double,
    val totalExpectedDelayMin: Int,
    val hopCount: Int
)