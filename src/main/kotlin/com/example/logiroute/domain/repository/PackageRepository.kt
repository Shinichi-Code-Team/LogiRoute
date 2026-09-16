package com.example.logiroute.domain.repository

import com.example.logiroute.domain.model.Package

interface PackageRepository {

    suspend fun getAllPackages(): List<Package>

    suspend fun getPackageById(id: String): Package?

    suspend fun createPackage(packageItem: Package): Package

    suspend fun updatePackage(
        id: String,
        packageItem: Package
    ): Package

    suspend fun deletePackage(id: String)
}