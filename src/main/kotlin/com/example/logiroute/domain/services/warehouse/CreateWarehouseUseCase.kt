package com.example.logiroute.domain.services.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.warehouse.WarehouseCreateValidator

class CreateWarehouseUseCase(
    private val repository: WarehouseRepository,
    private val validator: WarehouseCreateValidator
) {

    suspend operator fun invoke(warehouse: Warehouse): Warehouse {

        when (val result = validator.validate(warehouse)) {
            ValidationResult.Valid -> Unit

            is ValidationResult.Invalid -> {
                throw IllegalArgumentException(
                    "Invalid warehouse: ${result.errors}"
                )
            }
        }

        return repository.createWarehouse(warehouse)
    }
}