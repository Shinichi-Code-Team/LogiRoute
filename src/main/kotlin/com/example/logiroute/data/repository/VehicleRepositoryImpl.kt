package com.example.logiroute.data.repository

import com.example.logiroute.data.remote.datasource.RemoteVehicleDataSource
import com.example.logiroute.data.remote.dto.vehicle.VehicleResponseDto
import com.example.logiroute.data.remote.mapper.VehicleDtoMapper
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class VehicleRepositoryImpl(
    private val remoteDataSource: RemoteVehicleDataSource,
    private val warehouseRepository: WarehouseRepository,
    private val dtoMapper: VehicleDtoMapper
) : VehicleRepository {

    override fun getAllVehicles(): List<Vehicle> {
        val warehouseMap = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return remoteDataSource
            .getVehicles()
            .mapNotNull { dto ->
                mapRemoteVehicle(dto = dto, warehouseMap = warehouseMap)
            }
    }

    override fun addVehicle(vehicle: Vehicle): Boolean {
        TODO("Wire to Supabase create endpoint once RemoteVehicleDataSource supports it")
    }

    private fun mapRemoteVehicle(
        dto: VehicleResponseDto,
        warehouseMap: Map<String, Warehouse>
    ): Vehicle? {
        val currentHub = warehouseMap[dto.currentHubId] ?: return null

        val vehicle = dtoMapper.toDomain(dto = dto, currentHub = currentHub)
        currentHub.addVehicle(vehicle)

        return vehicle
    }
}