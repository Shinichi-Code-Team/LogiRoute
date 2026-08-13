package com.example.logiroute.domain.repository

import com.example.logiroute.data.dataholder.WarehouseRaw

interface WarehouseRepository {
    fun getWarehouses(): List<WarehouseRaw>
}