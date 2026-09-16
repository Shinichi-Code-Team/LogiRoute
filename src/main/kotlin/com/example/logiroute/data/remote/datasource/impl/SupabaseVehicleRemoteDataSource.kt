package com.example.logiroute.data.remote.datasource.vehicle

import com.example.logiroute.data.remote.dto.vehicle.CreateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.UpdateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.VehicleResponseDto
import com.example.logiroute.data.remote.provider.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

class SupabaseVehicleRemoteDataSource : RemoteVehicleDataSource {

    private val vehicleTable =
        SupabaseClientProvider.client.postgrest.from("vehicles")

    override suspend fun getVehicles(): List<VehicleResponseDto> {
        return vehicleTable
            .select()
            .decodeList<VehicleResponseDto>()
    }

    override suspend fun getVehicleById(
        id: String
    ): VehicleResponseDto? {

        return vehicleTable
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeSingleOrNull<VehicleResponseDto>()
    }

    override suspend fun createVehicle(
        request: CreateVehicleRequestDto
    ): VehicleResponseDto {

        return vehicleTable
            .insert(request) {
                select()
            }
            .decodeSingle<VehicleResponseDto>()
    }

    override suspend fun updateVehicle(
        id: String,
        request: UpdateVehicleRequestDto
    ): VehicleResponseDto {

        return vehicleTable
            .update(request) {
                filter {
                    eq("id", id)
                }

                select()
            }
            .decodeSingle<VehicleResponseDto>()
    }

    override suspend fun deleteVehicle(
        id: String
    ): Boolean {

        vehicleTable.delete {
            filter {
                eq("id", id)
            }
        }

        return true
    }
}