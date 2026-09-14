package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.dto.`package`.PackageResponseDto


interface RemotePackageDataSource {
     fun getPackages(): List<PackageResponseDto>
}