package com.example.logiroute.data.datasource.csv

import com.example.logiroute.data.dataholder.WarehouseRaw
import com.example.logiroute.data.datasource.WarehouseDataSource
import com.example.logiroute.data.processing.loader.Loader

class CsvWarehouseDataSource(
    private val loader: Loader
) : WarehouseDataSource {

    override fun getWarehouses(): List<WarehouseRaw> {
        return loader.loadWarehouses()
    }
}