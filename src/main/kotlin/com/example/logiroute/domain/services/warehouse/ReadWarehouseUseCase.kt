package com.example.logiroute.domain.services.warehouse

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.IdValidator
import com.example.logiroute.domain.validator.ValidationResult

class ReadWarehouseUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val idValidator: IdValidator
) {

    suspend operator fun invoke(
        id: String
    ): Result<Warehouse?> {

        return when (
            val validationResult = idValidator.validate(id)
        ) {
            ValidationResult.Valid -> {
                runCatching {
                    warehouseRepository.getWarehouseById(id)
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