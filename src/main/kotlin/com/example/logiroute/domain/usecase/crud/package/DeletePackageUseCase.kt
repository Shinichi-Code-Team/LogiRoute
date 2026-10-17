package com.example.logiroute.domain.usecase.crud.`package`

import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.util.suspendRunCatching
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules

class DeletePackageUseCase(
    private val packageRepository: PackageRepository,
    private val validationRules: ValidationRules,
    private val validationResultMapper: ValidationResultMapper
) {
    suspend operator fun invoke(id: String): Result<Unit> {
        val errors = listOfNotNull(
            validationRules.validatePackageId(id)
        )

        return when (val validationResult = validationResultMapper.map(errors)) {
            ValidationResult.Valid ->
                suspendRunCatching {
                    packageRepository.deletePackage(id)
                }

            is ValidationResult.Invalid ->
                Result.failure(
                    LogisticsException.EntityValidationException(validationResult.errors)
                )
        }
    }
}