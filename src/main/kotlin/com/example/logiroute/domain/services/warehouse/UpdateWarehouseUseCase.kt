package com.example.logiroute.domain.services.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.UpdateWarehouseInput
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.WarehouseUpdateValidator

class UpdateWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val warehouseUpdateValidator: WarehouseUpdateValidator
) {

    suspend operator fun invoke(
        id: String,
        input: UpdateWarehouseInput
    ): Warehouse {

        return when (
            val result = warehouseUpdateValidator.validate(id, input)
        ) {
            ValidationResult.Valid ->
                warehouseRepository.updateWarehouse(
                    id = id,
                    input = input
                )

            is ValidationResult.Invalid ->
                throw IllegalArgumentException(
                    result.errors.joinToString()
                )
        }
    }
}