package com.example.logiroute.data.repository

import com.example.logiroute.data.remote.datasource.vehicle.RemoteVehicleDataSource
import com.example.logiroute.data.remote.mapper.VehicleDtoMapper
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.request.UpdateVehicleInput
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.model.exceptions.LogisticsException

class VehicleRepositoryImpl(
    private val remoteDataSource: RemoteVehicleDataSource,
    private val warehouseRepository: WarehouseRepository,
    private val dtoMapper: VehicleDtoMapper
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

                dtoMapper.toDomain(
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
            .getWarehouseById(dto.currentHubId)
            ?: return null

        return dtoMapper.toDomain(
            dto = dto,
            warehouse = warehouse
        )
    }

    override suspend fun addVehicle(
        vehicle: Vehicle
    ): Vehicle {

        val request = dtoMapper.toCreateRequest(vehicle)

        val dto = remoteDataSource.createVehicle(request)

        return dtoMapper.toDomain(
            dto = dto,
            warehouse = vehicle.currentHub
        )
    }

    override suspend fun updateVehicle(
        id: String,
        input: UpdateVehicleInput
    ): Vehicle {

        val request = dtoMapper.toUpdateRequest(input)

        val dto = remoteDataSource.updateVehicle(
            id = id,
            request = request
        )

        val warehouse = warehouseRepository
            .getWarehouseById(dto.currentHubId)
            ?: throw LogisticsException.WarehouseNotFoundException(
                dto.currentHubId
            )

        return dtoMapper.toDomain(
            dto = dto,
            warehouse = warehouse
        )
    }

    override suspend fun deleteVehicle(
        id: String
    ) {
        remoteDataSource.deleteVehicle(id)
    }
}