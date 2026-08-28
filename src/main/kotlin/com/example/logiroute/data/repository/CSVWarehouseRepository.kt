package com.example.logiroute.data.repository

import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository

class CSVWarehouseRepository(
    private val loader: Loader
) : WarehouseRepository {

    private val warehouses: List<Warehouse> =
        loader.loadWarehouses().map { raw ->
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