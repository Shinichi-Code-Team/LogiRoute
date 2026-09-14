package com.example.logiroute.data.remote.datasource.impl

import com.example.logiroute.data.remote.datasource.RemoteVehicleDataSource
import com.example.logiroute.data.remote.dto.vehicle.VehicleResponseDto

class SupabaseVehicleRemoteDataSource : RemoteVehicleDataSource {

    override fun getVehicles(): List<VehicleResponseDto> {
        TODO("Connect to Supabase and fetch vehicles")
    }
}