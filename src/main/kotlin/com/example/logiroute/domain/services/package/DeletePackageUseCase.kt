package com.example.logiroute.domain.services.`package`

import com.example.logiroute.domain.repository.PackageRepository

class DeletePackageUseCase(
    private val packageRepository: PackageRepository
) {

    suspend operator fun invoke(id: String) {
        packageRepository.deletePackage(id)
    }
}