package com.example.logiroute.domain.services.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.request.UpdatePackageInput
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.packages.PackageUpdateValidator

class UpdatePackageUseCase(
    private val packageRepository: PackageRepository,
    private val packageUpdateValidator: PackageUpdateValidator
) {

    suspend operator fun invoke(
        id: String,
        input: UpdatePackageInput
    ): Package {

        val validationInput =
            PackageUpdateValidator.Input(
                id = id,
                update = input
            )

        return when (
            val result = packageUpdateValidator.validate(validationInput)
        ) {

            ValidationResult.Valid ->
                packageRepository.updatePackage(
                    id = id,
                    input = input
                )

            is ValidationResult.Invalid ->
                throw IllegalArgumentException(
                    result.errors.joinToString()
                )
        }
    }
}