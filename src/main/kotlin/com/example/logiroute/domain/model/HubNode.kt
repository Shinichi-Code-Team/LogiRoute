package com.example.logiroute.domain.model

import com.example.logiroute.com.example.logiroute.domain.model.HubType

data class HubNode(
    val warehouse: Warehouse,
    val hubType: HubType,
    val parentHub: HubNode? = null
)