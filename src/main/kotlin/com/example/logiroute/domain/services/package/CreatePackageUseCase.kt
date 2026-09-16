package com.example.logiroute.domain.services.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository

class CreatePackageUseCase(
    private val packageRepository: PackageRepository
) {

    suspend operator fun invoke(
        packageItem: Package
    ): Package {
        return packageRepository.createPackage(packageItem)
    }
}