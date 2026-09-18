package com.example.logiroute.domain.services.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.ValidationResult

class ReadPackageUseCase(
    private val packageRepository: PackageRepository,
    private val idValidator: IdValidator
) {

    suspend operator fun invoke(
        id: String
    ): Package? {

        return when (idValidator.validate(id)) {

            ValidationResult.Valid ->
                packageRepository.getPackageById(id)

            is ValidationResult.Invalid ->
                throw IllegalArgumentException(
                    "Invalid package ID"
                )
        }
    }
}