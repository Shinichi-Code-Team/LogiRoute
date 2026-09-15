package com.example.logiroute.data.repository

import com.example.logiroute.data.csv.datasource.CsvWarehouseDataSource
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository

class WarehouseRepositoryImpl(
    private val warehouseDataSource: CsvWarehouseDataSource
) : WarehouseRepository {

    private val warehouses: List<Warehouse> =
        warehouseDataSource.getWarehouses().map { raw ->
            Warehouse(
                id = raw.id,
                name = raw.name,
                regionalZone = raw.regionalZone,
                latitude = raw.latitude,
                longitude = raw.longitude
            )
        }

    override fun getAllWarehouses(): List<Warehouse> {
        return warehouses
    }
}