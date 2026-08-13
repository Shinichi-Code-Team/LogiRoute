package com.example.logiroute.data.repository

import com.example.logiroute.data.dataholder.WarehouseRaw
import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.domain.repository.WarehouseRepository


class CSVWarehouseRepository(private val loader: Loader) : WarehouseRepository {
    override fun getWarehouses(): List<WarehouseRaw> {
        return loader.loadWarehouses()
    }

}