package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.datasource.vehicle.RemoteVehicleDataSource
import com.example.logiroute.data.remote.dto.vehicle.CreateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.UpdateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.VehicleResponseDto
import com.example.logiroute.data.remote.ratelimit.RateLimiter
import com.example.logiroute.data.remote.retry.retryRemote
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupabaseVehicleRemoteDataSource(
    private val client: SupabaseClient,
    private val rateLimiter: RateLimiter
): RemoteVehicleDataSource {

    private val vehicleTable =
        client.postgrest.from("vehicles")

    override suspend fun getVehicles(): List<VehicleResponseDto> = withContext(Dispatchers.IO) {
        retryRemote(rateLimiter, operationName = "Vehicle.getVehicles") {
            vehicleTable.select().decodeList<VehicleResponseDto>()
        }
    }
    override suspend fun getVehicleById(
        id: String
    ): VehicleResponseDto? = withContext(Dispatchers.IO) {
        retryRemote(rateLimiter) {
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
        retryRemote(rateLimiter) {
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
        retryRemote(rateLimiter) {
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
            retryRemote (rateLimiter){
                vehicleTable.delete {
                    filter {
                        eq("id", id)
                    }
                }
            }
        }
    }
}