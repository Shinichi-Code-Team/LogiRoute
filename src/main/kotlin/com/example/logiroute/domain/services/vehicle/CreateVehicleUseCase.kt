package com.example.logiroute.domain.services.vehicle

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.validator.ValidationResult
import com.example.logiroute.domain.validator.VehicleCreateValidator

class CreateVehicleUseCase(
    private val vehicleRepository: VehicleRepository,
    private val vehicleCreateValidator: VehicleCreateValidator
) {

    suspend operator fun invoke(
        vehicle: Vehicle
    ): Result<Vehicle> {

        return when (
            val validationResult =
                vehicleCreateValidator.validate(vehicle)
        ) {
            ValidationResult.Valid -> {
                runCatching {
                    vehicleRepository.addVehicle(vehicle)
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
    }}