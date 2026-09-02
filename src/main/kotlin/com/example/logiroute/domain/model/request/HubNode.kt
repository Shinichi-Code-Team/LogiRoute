package com.example.logiroute.com.example.logiroute.domain.model.request

import com.example.logiroute.domain.model.Warehouse

class HubNode(
    val warehouse: Warehouse,
    val hubType: HubType,
    val parentHub: HubNode? = null,
) {
    val children: MutableList<HubNode> = mutableListOf()
}