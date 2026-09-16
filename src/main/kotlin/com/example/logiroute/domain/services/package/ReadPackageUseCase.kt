package com.example.logiroute.domain.services.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository

class ReadPackageUseCase(
    private val packageRepository: PackageRepository
) {

    suspend fun getAll(): List<Package> {
        return packageRepository.getAllPackages()
    }

    suspend fun getById(id: String): Package? {
        return packageRepository.getPackageById(id)
    }
}