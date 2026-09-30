package com.example.logiroute.domain.usecase.crud.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository

class CreatePackageUseCase(
    private val packageRepository: PackageRepository
) {

    suspend operator fun invoke(
        packageItem: Package
    ): Result<Package> =
        runCatching {
            packageRepository.createPackage(packageItem)
        }
}