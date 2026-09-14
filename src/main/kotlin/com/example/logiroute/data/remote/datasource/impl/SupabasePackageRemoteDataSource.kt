package com.example.logiroute.data.remote.datasource.impl

import com.example.logiroute.data.remote.datasource.RemotePackageDataSource
import com.example.logiroute.data.remote.dto.`package`.PackageResponseDto

class SupabasePackageRemoteDataSource : RemotePackageDataSource {

    override fun getPackages(): List<PackageResponseDto> {
        TODO("Connect to Supabase and fetch packages")
    }
}