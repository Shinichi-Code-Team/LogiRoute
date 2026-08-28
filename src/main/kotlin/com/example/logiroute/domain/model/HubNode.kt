package com.example.logiroute.domain.model

enum class HubType {
    GLOBAL_HUB,
    REGIONAL_CENTER,
    LOCAL_DEPOT
}

data class HubNode(
    val warehouse: Warehouse,
    val hubType: HubType,
    val parentHub: HubNode? = null
)