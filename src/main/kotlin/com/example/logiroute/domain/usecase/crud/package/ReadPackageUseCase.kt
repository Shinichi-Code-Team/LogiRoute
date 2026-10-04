package com.example.logiroute.domain.usecase.crud.`package`

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules

class ReadPackageUseCase(
    private val packageRepository: PackageRepository, private val validationRules: ValidationRules,
    private val validationResultMapper: ValidationResultMapper
) {
    suspend operator fun invoke(id: String): Result<Package?> {
        val errors = listOfNotNull(
            validationRules.validatePackageId(id)
        )

        val validationResult = validationResultMapper.map(errors)

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