package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.dto.warehouse.CreateWarehouseRequestDto
import com.example.logiroute.data.remote.dto.warehouse.UpdateWarehouseRequestDto
import com.example.logiroute.data.remote.dto.warehouse.WarehouseResponseDto

interface RemoteWarehouseDataSource {

    suspend fun getWarehouses(): List<WarehouseResponseDto>

    suspend fun getWarehouseById(
        id: String
    ): WarehouseResponseDto?

    suspend fun createWarehouse(
        request: CreateWarehouseRequestDto
    ): WarehouseResponseDto

    suspend fun updateWarehouse(
        id: String,
        request: UpdateWarehouseRequestDto
    ): WarehouseResponseDto

    suspend fun deleteWarehouse(
        id: String
    )
}