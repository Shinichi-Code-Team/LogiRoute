package com.example.logiroute.data.remote.datasource.vehicle

import com.example.logiroute.data.remote.dto.vehicle.CreateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.UpdateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.VehicleResponseDto

interface RemoteVehicleDataSource {

    suspend fun getVehicles(): List<VehicleResponseDto>

    suspend fun getVehicleById(id: String): VehicleResponseDto?

    suspend fun createVehicle(
        request: CreateVehicleRequestDto
    ): VehicleResponseDto

    suspend fun updateVehicle(
        id: String,
        request: UpdateVehicleRequestDto
    ): VehicleResponseDto

    suspend fun deleteVehicle(id: String): Boolean
}