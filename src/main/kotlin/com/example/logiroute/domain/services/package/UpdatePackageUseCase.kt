package com.example.logiroute.domain.services.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.request.UpdatePackageInput
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validator.PackageUpdateValidator

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val packageUpdateValidator: PackageUpdateValidator
) {

    suspend operator fun invoke(
        id: String,
        input: UpdatePackageInput
    ): Result<Package> {

        val validationInput = PackageUpdateValidator.inputValidator(
            id = id,
            update = input
        )

        return when (
            val validationResult =
                packageUpdateValidator.validate(validationInput)
        ) {
            ValidationResult.Valid -> {
                runCatching {
                    packageRepository.updatePackage(
                        id = id,
                        input = input
                    )
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