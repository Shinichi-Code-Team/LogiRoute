package com.example.logiroute.data.repository

import com.example.logiroute.data.remote.datasource.RemoteWarehouseDataSource
import com.example.logiroute.data.remote.mapper.WarehouseDtoMapper
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository

class WarehouseRepositoryImpl(
    private val remoteDataSource: RemoteWarehouseDataSource,
    private val dtoMapper: WarehouseDtoMapper
) : WarehouseRepository {

    override fun getAllWarehouses(): List<Warehouse> {
        return remoteDataSource
            .getWarehouses()
            .map { dto ->
                dtoMapper.toDomain(dto)
            }
    }
}