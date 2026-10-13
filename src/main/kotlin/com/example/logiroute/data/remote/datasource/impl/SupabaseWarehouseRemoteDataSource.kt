package com.example.logiroute.data.remote.datasource.impl

import com.example.logiroute.data.remote.datasource.RemoteWarehouseDataSource
import com.example.logiroute.data.remote.dto.warehouse.CreateWarehouseRequestDto
import com.example.logiroute.data.remote.dto.warehouse.UpdateWarehouseRequestDto
import com.example.logiroute.data.remote.dto.warehouse.WarehouseResponseDto
import com.example.logiroute.data.remote.retry.retryRemote
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
class SupabaseWarehouseRemoteDataSource(
    private val client: SupabaseClient
) : RemoteWarehouseDataSource {

    private val warehouseTable =
        client.postgrest.from("warehouses")

    override suspend fun getWarehouses(): List<WarehouseResponseDto> {
        return retryRemote(operationName = "Warehouse.getWarehouses") {
            warehouseTable.select().decodeList<WarehouseResponseDto>()
        }
    }

    override suspend fun getWarehouseById(
        id: String
    ): WarehouseResponseDto? = withContext(Dispatchers.IO) {
        retryRemote {
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
    ): WarehouseResponseDto = withContext(Dispatchers.IO) {
        retryRemote {
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
    ): WarehouseResponseDto = withContext(Dispatchers.IO) {
        retryRemote {
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
        withContext(Dispatchers.IO) {
        retryRemote {
            warehouseTable.delete {
                filter {
                    eq("id", id)
                }
            }
        }
        }
    }
}