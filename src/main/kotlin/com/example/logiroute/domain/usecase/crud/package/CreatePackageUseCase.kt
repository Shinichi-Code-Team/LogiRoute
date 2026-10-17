package com.example.logiroute.domain.usecase.crud.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.util.suspendRunCatching

class CreatePackageUseCase(
    private val packageRepository: PackageRepository
) {

    suspend operator fun invoke(
        packageItem: Package
    ): Result<Package> =
        suspendRunCatching {
            packageRepository.createPackage(packageItem)
        }
}