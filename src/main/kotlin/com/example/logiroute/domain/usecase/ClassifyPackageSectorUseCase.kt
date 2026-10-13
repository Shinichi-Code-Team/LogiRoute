package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.repository.PackageRepository

class ClassifyPackageSectorUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(packageId: String): Result<String> {
        TODO("Not implemented yet")
    }
}
