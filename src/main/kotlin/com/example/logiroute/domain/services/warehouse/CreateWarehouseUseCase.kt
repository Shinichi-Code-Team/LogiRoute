package com.example.logiroute.domain.services.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.WarehouseCreateValidator

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val warehouseCreateValidator: WarehouseCreateValidator
) {

    suspend operator fun invoke(
        warehouse: Warehouse
    ): Result<Warehouse> {

        return when (
            val validationResult =
                warehouseCreateValidator.validate(warehouse)
        ) {
            ValidationResult.Valid -> {
                runCatching {
                    warehouseRepository.createWarehouse(warehouse)
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