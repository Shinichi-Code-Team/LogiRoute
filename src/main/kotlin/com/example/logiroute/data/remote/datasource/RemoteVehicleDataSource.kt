package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.dto.vehicle.VehicleResponseDto

interface RemoteVehicleDataSource {
    fun getVehicles(): List<VehicleResponseDto>
}