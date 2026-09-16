package com.example.logiroute.data.repository

import com.example.logiroute.data.remote.datasource.RemoteWarehouseDataSource
import com.example.logiroute.data.remote.mapper.WarehouseDtoMapper
import com.example.logiroute.data.remote.dto.warehouse.CreateWarehouseRequestDto
import com.example.logiroute.data.remote.dto.warehouse.UpdateWarehouseRequestDto
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository

class WarehouseRepositoryImpl(
    private val remoteDataSource: RemoteWarehouseDataSource,
    private val dtoMapper: WarehouseDtoMapper
) : WarehouseRepository {

    override suspend fun getAllWarehouses(): List<Warehouse> {
        return remoteDataSource
            .getWarehouses()
            .map { dtoMapper.toDomain(it) }
    }

    override suspend fun getWarehouseById(
        id: String
    ): Warehouse? {
        val dto = remoteDataSource.getWarehouseById(id)
            ?: return null

        return dtoMapper.toDomain(dto)
    }

    override suspend fun createWarehouse(
        warehouse: Warehouse
    ): Warehouse {

        val request = dtoMapper.toCreateRequest(warehouse)

        val dto = remoteDataSource.createWarehouse(request)

        return dtoMapper.toDomain(dto)
    }

    override suspend fun updateWarehouse(
        id: String,
        warehouse: Warehouse
    ): Warehouse {

        val request = dtoMapper.toUpdateRequest(warehouse)

        val dto = remoteDataSource.updateWarehouse(
            id = id,
            request = request
        )

        return dtoMapper.toDomain(dto)
    }

    override suspend fun deleteWarehouse(
        id: String
    ) {
        remoteDataSource.deleteWarehouse(id)
    }
}