package com.example.logiroute.domain.services.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.warehouse.WarehouseCreateValidator

class CreateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val warehouseCreateValidator: WarehouseCreateValidator
) {

    suspend operator fun invoke(
        warehouse: Warehouse
    ): Warehouse {

        return when (
            val result = warehouseCreateValidator.validate(warehouse)
        ) {
            ValidationResult.Valid ->
                warehouseRepository.createWarehouse(warehouse)

            is ValidationResult.Invalid ->
                throw IllegalArgumentException(
                    result.errors.joinToString()
                )
        }
    }
}