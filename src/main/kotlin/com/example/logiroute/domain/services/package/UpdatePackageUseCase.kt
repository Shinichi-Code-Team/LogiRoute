package com.example.logiroute.domain.services.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository
) {

    suspend operator fun invoke(
        id: String,
        packageItem: Package
    ): Package {
        return packageRepository.updatePackage(
            id,
            packageItem
        )
    }
}