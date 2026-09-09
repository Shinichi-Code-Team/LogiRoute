package com.example.logiroute.com.example.logiroute.domain.model.result

data class TreePerformanceReport(
    val sampleKeys: List<String>,
    val unbalancedSteps: Map<String, Int>,
    val balancedSteps: Map<String, Int>,
    val unbalancedHeight: Int,
    val balancedHeight: Int
)