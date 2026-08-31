package com.example.logiroute.domain.model.exceptions

class WarehouseNotFoundException (
    warehouseId: String
) : RuntimeException("Warehouse not found: $warehouseId")