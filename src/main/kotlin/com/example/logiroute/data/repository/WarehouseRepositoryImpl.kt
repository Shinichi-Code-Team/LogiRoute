package com.example.logiroute.data.repository

import com.example.logiroute.data.datasource.WarehouseDataSource
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository

class WarehouseRepositoryImpl(
    private val warehouseDataSource: WarehouseDataSource
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