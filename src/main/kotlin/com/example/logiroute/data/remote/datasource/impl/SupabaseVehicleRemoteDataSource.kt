package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.datasource.vehicle.RemoteVehicleDataSource
import com.example.logiroute.data.remote.dto.vehicle.CreateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.UpdateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.VehicleResponseDto
import com.example.logiroute.data.remote.provider.SupabaseClientProvider
import com.example.logiroute.data.remote.retry.retryRemote
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

class SupabaseVehicleRemoteDataSource : RemoteVehicleDataSource {

    private val vehicleTable =
        SupabaseClientProvider.client.postgrest.from("vehicles")

    override suspend fun getVehicles(): List<VehicleResponseDto> {
        return retryRemote {
            vehicleTable
                .select()
                .decodeList<VehicleResponseDto>()
        }
    }

    override suspend fun getVehicleById(
        id: String
    ): VehicleResponseDto? {
        return retryRemote {
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
    ): VehicleResponseDto {
        return retryRemote {
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
    ): VehicleResponseDto {
        return retryRemote {
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
        retryRemote {
            vehicleTable.delete {
                filter {
                    eq("id", id)
                }
            }
        }
    }
}