package com.example.logiroute.data.remote.datasource.impl

import com.example.logiroute.data.remote.datasource.RemoteWarehouseDataSource
import com.example.logiroute.data.remote.dto.warehouse.CreateWarehouseRequestDto
import com.example.logiroute.data.remote.dto.warehouse.UpdateWarehouseRequestDto
import com.example.logiroute.data.remote.dto.warehouse.WarehouseResponseDto
import com.example.logiroute.data.remote.provider.SupabaseClientProvider
import com.example.logiroute.data.remote.retry.retryRemote
import io.github.jan.supabase.postgrest.postgrest

class SupabaseWarehouseRemoteDataSource : RemoteWarehouseDataSource {

    private val warehouseTable =
        SupabaseClientProvider.client.postgrest.from("warehouses")

    override suspend fun getWarehouses(): List<WarehouseResponseDto> {
        return retryRemote {
            warehouseTable
                .select()
                .decodeList<WarehouseResponseDto>()
        }
    }

    override suspend fun getWarehouseById(
        id: String
    ): WarehouseResponseDto? {
        return retryRemote {
            warehouseTable
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeList<WarehouseResponseDto>()
                .firstOrNull()
        }
    }

    override suspend fun createWarehouse(
        request: CreateWarehouseRequestDto
    ): WarehouseResponseDto {
        return retryRemote {
            warehouseTable
                .insert(request) {
                    select()
                }
                .decodeSingle<WarehouseResponseDto>()
        }
    }

    override suspend fun updateWarehouse(
        id: String,
        request: UpdateWarehouseRequestDto
    ): WarehouseResponseDto {
        return retryRemote {
            warehouseTable
                .update(request) {
                    filter {
                        eq("id", id)
                    }
                    select()
                }
                .decodeSingle<WarehouseResponseDto>()
        }
    }

    override suspend fun deleteWarehouse(
        id: String
    ) {
        retryRemote {
            warehouseTable.delete {
                filter {
                    eq("id", id)
                }
            }
        }
    }
}