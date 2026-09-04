package com.example.logiroute.domain.model.response

data class TreePerformanceReport(
    val sampleKeys: List<String>,
    val unbalancedSteps: Map<String, Int>,
    val balancedSteps: Map<String, Int>,
    val unbalancedHeight: Int,
    val balancedHeight: Int
)