package com.example.logiroute.domain.model.request

import com.example.logiroute.domain.model.Priority

data class UpdatePackageInput(
    val weight: Double? = null,
    val originHubId: String? = null,
    val destinationHubId: String? = null,
    val priority: Priority? = null
)