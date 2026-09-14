package com.example.logiroute.data.csv.datasource.impl

import com.example.logiroute.data.csv.datasource.CsvWarehouseDataSource
import com.example.logiroute.data.csv.processing.loader.Loader
import com.example.logiroute.data.csv.raw.WarehouseRaw

class FileCsvWarehouseDataSource(
    private val loader: Loader
) : CsvWarehouseDataSource {

    override fun getWarehouses(): List<WarehouseRaw> {
        return loader.loadWarehouses()
    }
}