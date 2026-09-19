package com.example.logiroute.domain.repository

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.request.UpdatePackageInput

interface PackageRepository {

    suspend fun getAllPackages(): List<Package>

    suspend fun getPackageById(id: String): Package?

    suspend fun createPackage(packageItem: Package): Package

    suspend fun updatePackage(
        id: String,
        input: UpdatePackageInput
    ): Package

    suspend fun deletePackage(id: String)
}