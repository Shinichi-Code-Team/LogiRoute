package com.example.logiroute.domain.services.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.ValidationResult

class ReadWarehouseUseCase(
    private val repository: WarehouseRepository,
    private val idValidator: IdValidator
) {

    suspend operator fun invoke(id: String): Warehouse? {

        when (idValidator.validate(id)) {
            ValidationResult.Valid -> Unit

            is ValidationResult.Invalid -> {
                throw IllegalArgumentException(
                    "Invalid warehouse ID: $id"
                )
            }
        }

        return repository.getWarehouseById(id)
    }
}