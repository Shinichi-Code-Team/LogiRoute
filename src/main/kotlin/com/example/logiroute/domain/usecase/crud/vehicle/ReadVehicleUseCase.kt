package com.example.logiroute.domain.usecase.crud.vehicle

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.validation.ValidationResult
import com.example.logiroute.domain.validation.ValidationResultMapper
import com.example.logiroute.domain.validation.ValidationRules

class ReadVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val validationRules: ValidationRules,
    private val validationResultMapper: ValidationResultMapper
) {

    suspend operator fun invoke(id: String): Result<Vehicle?> {
        val errors = listOfNotNull(
            validationRules.validateVehicleId(id)
        )
        val validationResult = validationResultMapper.map(errors)
        return when (validationResult) {
            ValidationResult.Valid ->
                runCatching {
                    vehicleRepository.getVehicleById(id)
                }

            is ValidationResult.Invalid ->
                Result.failure(
                    LogisticsException.EntityValidationException(validationResult.errors)
                )
            }
        }
    }
