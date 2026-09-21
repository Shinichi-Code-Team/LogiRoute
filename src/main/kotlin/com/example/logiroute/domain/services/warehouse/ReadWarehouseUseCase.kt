package com.example.logiroute.domain.services.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.ValidationRules
import com.example.logiroute.domain.validator.toValidationResult

class ReadWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository
) {

    suspend operator fun invoke(id: String): Result<Warehouse?> {
        val validationResult = listOfNotNull(
            ValidationRules.validateWarehouseId(id)
        ).toValidationResult()

        return when (validationResult) {
            ValidationResult.Valid ->
                runCatching {
                    warehouseRepository.getWarehouseById(id)
                }

            is ValidationResult.Invalid ->
                Result.failure(
                    LogisticsException.EntityValidationException(validationResult.errors)
                )
        }
    }
}