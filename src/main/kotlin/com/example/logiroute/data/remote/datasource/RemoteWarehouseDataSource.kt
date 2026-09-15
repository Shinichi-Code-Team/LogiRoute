package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.dto.warehouse.WarehouseResponseDto

interface RemoteWarehouseDataSource {
    fun getWarehouses(): List<WarehouseResponseDto>
}