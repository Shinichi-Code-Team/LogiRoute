package com.example.logiroute.data.remote.datasource.impl

import com.example.logiroute.data.remote.datasource.RemotePackageDataSource
import com.example.logiroute.data.remote.dto.`package`.CreatePackageRequestDto
import com.example.logiroute.data.remote.dto.`package`.PackageResponseDto
import com.example.logiroute.data.remote.dto.`package`.UpdatePackageRequestDto
import com.example.logiroute.data.remote.retry.retryRemote
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupabasePackageRemoteDataSource(
    private val client: SupabaseClient
) : RemotePackageDataSource {

    private val packageTable = client.postgrest.from("packages")

    override suspend fun getPackages(): List<PackageResponseDto> =
        withContext(Dispatchers.IO) {
            retryRemote(operationName = "Package.getPackages") {
                packageTable
                    .select()
                    .decodeList<PackageResponseDto>()
            }
        }
    override suspend fun getPackageById(
        id: String
    ): PackageResponseDto? = withContext(Dispatchers.IO) {
        retryRemote {
            packageTable
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeList<PackageResponseDto>()
                .firstOrNull()
        }
    }


    override suspend fun createPackage(
        request: CreatePackageRequestDto
    ): PackageResponseDto = withContext(Dispatchers.IO) {
        retryRemote {
            packageTable
                .insert(request) {
                    select()
                }
                .decodeSingle<PackageResponseDto>()
        }
    }

    override suspend fun updatePackage(
        id: String,
        request: UpdatePackageRequestDto
    ): PackageResponseDto = withContext(Dispatchers.IO) {
        retryRemote {
            packageTable
                .update(request) {
                    filter {
                        eq("id", id)
                    }
                    select()
                }
                .decodeSingle<PackageResponseDto>()
        }
    }

    override suspend fun deletePackage(id: String) {
        withContext(Dispatchers.IO) {
            retryRemote {
            packageTable.delete {
                filter {
                    eq("id", id)
                }
            }
        }
    }
    }
}