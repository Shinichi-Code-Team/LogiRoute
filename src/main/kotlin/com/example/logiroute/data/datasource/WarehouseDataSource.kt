package com.example.logiroute.data.datasource

import com.example.logiroute.data.dataholder.WarehouseRaw

interface WarehouseDataSource {
    fun getWarehouses(): List<WarehouseRaw>
}