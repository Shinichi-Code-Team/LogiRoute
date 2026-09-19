package com.example.logiroute.domain.services.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.PackageCreateValidator

class CreatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val packageCreateValidator: PackageCreateValidator
) {

    suspend operator fun invoke(
        packageItem: Package
    ): Result<Package> {

        return when (
            val validationResult =
                packageCreateValidator.validate(packageItem)
        ) {
            ValidationResult.Valid -> {
                runCatching {
                    packageRepository.createPackage(packageItem)
                }
            }

            is ValidationResult.Invalid -> {
                Result.failure(
                    LogisticsException.EntityValidationException(
                        validationResult.errors
                    )
                )
            }
        }
    }
}