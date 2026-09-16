package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.dto.`package`.CreatePackageRequestDto
import com.example.logiroute.data.remote.dto.`package`.PackageResponseDto
import com.example.logiroute.data.remote.dto.`package`.UpdatePackageRequestDto

interface RemotePackageDataSource {

     suspend fun getPackages(): List<PackageResponseDto>

     suspend fun getPackageById(id: String): PackageResponseDto?

     suspend fun createPackage(
          request: CreatePackageRequestDto
     ): PackageResponseDto

     suspend fun updatePackage(
          id: String,
          request: UpdatePackageRequestDto
     ): PackageResponseDto

     suspend fun deletePackage(id: String)
}