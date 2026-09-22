package com.example.logiroute.data.remote.datasource.impl

import com.example.logiroute.data.remote.datasource.RemotePackageDataSource
import com.example.logiroute.data.remote.dto.`package`.CreatePackageRequestDto
import com.example.logiroute.data.remote.dto.`package`.PackageResponseDto
import com.example.logiroute.data.remote.dto.`package`.UpdatePackageRequestDto
import com.example.logiroute.data.remote.provider.SupabaseClientProvider
import com.example.logiroute.data.remote.retry.retryRemote
import io.github.jan.supabase.postgrest.postgrest

class SupabasePackageRemoteDataSource : RemotePackageDataSource {

    private val packageTable =
        SupabaseClientProvider.client.postgrest.from("packages")

    override suspend fun getPackages(): List<PackageResponseDto> {
        return retryRemote {
            packageTable
                .select()
                .decodeList<PackageResponseDto>()
        }
    }

    override suspend fun getPackageById(
        id: String
    ): PackageResponseDto? {
        return retryRemote {
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
    ): PackageResponseDto {
        return retryRemote {
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
    ): PackageResponseDto {
        return retryRemote {
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
        retryRemote {
            packageTable.delete {
                filter {
                    eq("id", id)
                }
            }
        }
    }
}