package com.example.logiroute.data.remote.datasource.impl

import com.example.logiroute.data.remote.datasource.RemoteWarehouseDataSource
import com.example.logiroute.data.remote.dto.warehouse.WarehouseResponseDto

class SupabaseWarehouseRemoteDataSource : RemoteWarehouseDataSource {

    override fun getWarehouses(): List<WarehouseResponseDto> {
        TODO("Connect to Supabase and fetch warehouses")
    }
}