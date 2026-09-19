package com.example.logiroute.domain.services.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.ValidationResult

class ReadPackageUseCase(
    private val packageRepository: PackageRepository,
    private val idValidator: IdValidator
) {

    suspend operator fun invoke(
        id: String
    ): Result<Package?> {

        return when (
            val validationResult = idValidator.validate(id)
        ) {
            ValidationResult.Valid -> {
                runCatching {
                    packageRepository.getPackageById(id)
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