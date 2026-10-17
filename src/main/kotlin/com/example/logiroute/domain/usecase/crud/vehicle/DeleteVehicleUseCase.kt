package com.example.logiroute.domain.usecase.crud.vehicle

import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.util.suspendRunCatching
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validationRules: ValidationRules,
    private val validationResultMapper: ValidationResultMapper
) {

    suspend operator fun invoke(id: String): Result<Unit> {
        val errors = listOfNotNull(
            validationRules.validateVehicleId(id)
        )
        return when (val validationResult = validationResultMapper.map(errors)) {
            ValidationResult.Valid ->
                suspendRunCatching {
                    vehicleRepository.deleteVehicle(id)
                    Unit
                }

            is ValidationResult.Invalid ->
                Result.failure(
                    LogisticsException.EntityValidationException(validationResult.errors)
                )
            }
        }
    }
