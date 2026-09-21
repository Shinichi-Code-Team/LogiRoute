package com.example.logiroute.domain.services.warehouse

import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.ValidationRules
import com.example.logiroute.domain.validator.toValidationResult

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {

    suspend operator fun invoke(id: String): Result<Unit> {
        val validationResult = listOfNotNull(
            ValidationRules.validateWarehouseId(id)
        ).toValidationResult()

        return when (validationResult) {
            ValidationResult.Valid ->
                runCatching {
                    warehouseRepository.deleteWarehouse(id)
                }

            is ValidationResult.Invalid ->
                Result.failure(
                    LogisticsException.EntityValidationException(validationResult.errors)
                )
        }
    }
}