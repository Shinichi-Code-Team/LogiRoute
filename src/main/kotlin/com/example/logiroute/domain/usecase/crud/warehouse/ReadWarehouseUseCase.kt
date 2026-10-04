package com.example.logiroute.domain.usecase.crud.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules

class ReadWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val validationRules: ValidationRules,
    private val validationResultMapper: ValidationResultMapper
) {

    suspend operator fun invoke(id: String): Result<Warehouse?> {
        val errors = listOfNotNull(
            validationRules.validateWarehouseId(id)
        )
        val validationResult = validationResultMapper.map(errors)
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