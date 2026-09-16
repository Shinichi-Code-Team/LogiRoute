package com.example.logiroute.data.remote.mapper

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

    fun toDto(warehouse: Warehouse): WarehouseResponseDto {
        return WarehouseResponseDto(
            id = warehouse.id,
            name = warehouse.name,
            regionalZone = warehouse.regionalZone,
            latitude = warehouse.latitude,
            longitude = warehouse.longitude
        )
    }
}