package com.example.logiroute.data.repository

import com.example.logiroute.data.remote.datasource.vehicle.RemoteVehicleDataSource
import com.example.logiroute.data.remote.mapper.VehicleDtoMapper
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class VehicleRepositoryImpl(
    private val remoteDataSource: RemoteVehicleDataSource,
    private val warehouseRepository: WarehouseRepository,
    private val mapper: VehicleDtoMapper
) : VehicleRepository {

    override suspend fun getAllVehicles(): List<Vehicle> {

        val warehouses = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return remoteDataSource
            .getVehicles()
            .mapNotNull { dto ->

                val warehouse = warehouses[dto.currentHubId]
                    ?: return@mapNotNull null

                mapper.toDomain(
                    dto = dto,
                    warehouse = warehouse
                )
            }
    }

    override suspend fun getVehicleById(
        id: String
    ): Vehicle? {

        val dto = remoteDataSource
            .getVehicleById(id)
            ?: return null

        val warehouse = warehouseRepository
            .getAllWarehouses()
            .firstOrNull { it.id == dto.currentHubId }
            ?: return null

        return mapper.toDomain(
            dto = dto,
            warehouse = warehouse
        )
    }

    override suspend fun addVehicle(
        vehicle: Vehicle
    ): Boolean {

        mapper
            .toCreateRequest(vehicle)
            .let {
                remoteDataSource.createVehicle(it)
            }

        return true
    }

    override suspend fun updateVehicle(
        vehicle: Vehicle
    ): Boolean {

        mapper
            .toUpdateRequest(vehicle)
            .let {
                remoteDataSource.updateVehicle(
                    id = vehicle.id,
                    request = it
                )
            }

        return true
    }

    override suspend fun deleteVehicle(
        id: String
    ): Boolean {

        return remoteDataSource.deleteVehicle(id)
    }
}