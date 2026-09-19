package com.example.logiroute.domain.repository

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.UpdateWarehouseInput

interface WarehouseRepository {

    suspend fun getAllWarehouses(): List<Warehouse>

    suspend fun getWarehouseById(
        id: String
    ): Warehouse?

    suspend fun createWarehouse(
        warehouse: Warehouse
    ): Warehouse

    suspend fun updateWarehouse(
        id: String,
        input: UpdateWarehouseInput
    ): Warehouse

    suspend fun deleteWarehouse(
        id: String
    )
}