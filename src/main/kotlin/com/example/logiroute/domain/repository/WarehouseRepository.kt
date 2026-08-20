package com.example.logiroute.domain.repository

import com.example.logiroute.domain.model.Warehouse

interface WarehouseRepository {
    fun getAllWarehouses(): List<Warehouse>
}