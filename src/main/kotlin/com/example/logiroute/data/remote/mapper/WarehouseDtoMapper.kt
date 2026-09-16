package com.example.logiroute.data.remote.mapper

import com.example.logiroute.data.remote.dto.warehouse.CreateWarehouseRequestDto
import com.example.logiroute.data.remote.dto.warehouse.UpdateWarehouseRequestDto
import com.example.logiroute.data.remote.dto.warehouse.WarehouseResponseDto
import com.example.logiroute.domain.model.Warehouse

class WarehouseDtoMapper {

    fun toDomain(dto: WarehouseResponseDto): Warehouse {
        return Warehouse(
            id = dto.id,
            name = dto.name,
            regionalZone = dto.regionalZone,
            latitude = dto.latitude,
            longitude = dto.longitude
        )
    }

    fun toCreateRequest(
        warehouse: Warehouse
    ): CreateWarehouseRequestDto {
        return CreateWarehouseRequestDto(
            id = warehouse.id,
            name = warehouse.name,
            regionalZone = warehouse.regionalZone,
            latitude = warehouse.latitude,
            longitude = warehouse.longitude
        )
    }

    fun toUpdateRequest(
        warehouse: Warehouse
    ): UpdateWarehouseRequestDto {
        return UpdateWarehouseRequestDto(
            name = warehouse.name,
            regionalZone = warehouse.regionalZone,
            latitude = warehouse.latitude,
            longitude = warehouse.longitude
        )
    }
}