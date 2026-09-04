package com.example.logiroute.com.example.logiroute.domain.model.request


data class HubHierarchyRaw(
    val warehouseId: String,
    val hubType: HubType,
    val parentWarehouseId: String?
)
