package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.datasource.vehicle.RemoteVehicleDataSource
import com.example.logiroute.data.remote.dto.vehicle.CreateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.UpdateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.VehicleResponseDto
import com.example.logiroute.data.remote.retry.retryRemote
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupabaseVehicleRemoteDataSource (    private val client: SupabaseClient
): RemoteVehicleDataSource {

    private val vehicleTable =
        client.postgrest.from("vehicles")

    override suspend fun getVehicles(): List<VehicleResponseDto> = withContext(Dispatchers.IO) {
        retryRemote(operationName = "Vehicle.getVehicles") {
            vehicleTable.select().decodeList<VehicleResponseDto>()
        }
    }
    override suspend fun getVehicleById(
        id: String
    ): VehicleResponseDto? = withContext(Dispatchers.IO) {
        retryRemote {
            vehicleTable
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeSingleOrNull<VehicleResponseDto>()
        }
    }

    override suspend fun createVehicle(
        request: CreateVehicleRequestDto
    ): VehicleResponseDto = withContext(Dispatchers.IO) {
        retryRemote {
            vehicleTable
                .insert(request) {
                    select()
                }
                .decodeSingle<VehicleResponseDto>()
        }
    }

    override suspend fun updateVehicle(
        id: String,
        request: UpdateVehicleRequestDto
    ): VehicleResponseDto = withContext(Dispatchers.IO) {
        retryRemote {
            vehicleTable
                .update(request) {
                    filter {
                        eq("id", id)
                    }
                    select()
                }
                .decodeSingle<VehicleResponseDto>()
        }
    }

    override suspend fun deleteVehicle(
        id: String
    ) {
        withContext(Dispatchers.IO) {
            retryRemote {
                vehicleTable.delete {
                    filter {
                        eq("id", id)
                    }
                }
            }
        }
    }
}