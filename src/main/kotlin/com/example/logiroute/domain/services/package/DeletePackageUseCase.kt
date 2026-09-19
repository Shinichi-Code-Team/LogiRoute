package com.example.logiroute.domain.services.`package`

import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.ValidationResult

class DeletePackageUseCase(
    private val packageRepository: PackageRepository,
    private val idValidator: IdValidator
) {

    suspend operator fun invoke(
        id: String
    ) {

        when (idValidator.validate(id)) {

            ValidationResult.Valid ->
                packageRepository.deletePackage(id)

            is ValidationResult.Invalid ->
                throw IllegalArgumentException(
                    "Invalid package ID"
                )
        }
    }
}