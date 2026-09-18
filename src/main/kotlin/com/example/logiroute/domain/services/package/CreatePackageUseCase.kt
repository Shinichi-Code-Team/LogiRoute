package com.example.logiroute.domain.services.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.packages.PackageCreateValidator

class CreatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val packageCreateValidator: PackageCreateValidator
) {

    suspend operator fun invoke(
        packageItem: Package
    ): Package {

        return when (
            val result = packageCreateValidator.validate(packageItem)
        ) {

            ValidationResult.Valid ->
                packageRepository.createPackage(packageItem)

            is ValidationResult.Invalid ->
                throw IllegalArgumentException(
                    result.errors.joinToString()
                )
        }
    }
}