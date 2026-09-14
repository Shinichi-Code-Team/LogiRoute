package com.example.logiroute.data.csv.datasource

import com.example.logiroute.data.csv.raw.WarehouseRaw

interface CsvWarehouseDataSource {
    fun getWarehouses(): List<WarehouseRaw>
}