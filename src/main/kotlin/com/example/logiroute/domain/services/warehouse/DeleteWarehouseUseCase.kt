package com.example.logiroute.domain.services.warehouse

import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.ValidationResult

class DeleteWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: IdValidator
) {

    suspend operator fun invoke(
        id: String
    ) {

        when (idValidator.validate(id)) {

            ValidationResult.Valid ->
                warehouseRepository.deleteWarehouse(id)

            is ValidationResult.Invalid ->
                throw IllegalArgumentException("Invalid warehouse ID")
        }
    }
}