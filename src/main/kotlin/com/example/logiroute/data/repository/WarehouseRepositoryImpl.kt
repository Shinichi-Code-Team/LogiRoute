package com.example.logiroute.data.repository

import com.example.logiroute.data.remote.datasource.RemoteWarehouseDataSource
import com.example.logiroute.data.remote.mapper.WarehouseDtoMapper
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.UpdateWarehouseInput
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.data.remote.dto.warehouse.WarehouseResponseDto

class WarehouseRepositoryImpl(
    private val remoteDataSource: RemoteWarehouseDataSource,
    private val dtoMapper: WarehouseDtoMapper
) : WarehouseRepository {

    override suspend fun getAllWarehouses(): List<Warehouse> {
        return remoteDataSource.getWarehouses().mapNotNull { dto ->
            val missing = missingFields(dto)
            if (missing.isNotEmpty()) {
                System.err.println("Skipping warehouse ${dto.id}: missing ${missing.joinToString()}")
                null
            } else {
                dtoMapper.toDomain(dto)
            }
        }
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
        input: UpdateWarehouseInput
    ): Warehouse {

        val request = dtoMapper.toUpdateRequest(input)

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
    private fun missingFields(dto: WarehouseResponseDto): List<String> =
        buildList {
            if (dto.regionalZone.isNullOrBlank()) add("regionalZone")
            if (dto.latitude == null) add("latitude")
            if (dto.longitude == null) add("longitude")
        }
}