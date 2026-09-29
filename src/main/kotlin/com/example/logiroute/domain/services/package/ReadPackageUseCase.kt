package com.example.logiroute.domain.services.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationRules
import com.example.logiroute.domain.validation.toValidationResult

class ReadPackageUseCase(
    private val packageRepository: PackageRepository
) {
    suspend operator fun invoke(id: String): Result<Package?> {
        val validationResult = listOfNotNull(
            ValidationRules.validatePackageId(id)
        ).toValidationResult()

        return when (validationResult) {
            ValidationResult.Valid ->
                runCatching {
                    packageRepository.getPackageById(id)
                }

            is ValidationResult.Invalid ->
                Result.failure(
                    LogisticsException.EntityValidationException(validationResult.errors)
                )
        }
    }
}