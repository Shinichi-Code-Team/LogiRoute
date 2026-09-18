package com.example.logiroute.domain.validator

sealed interface VehicleValidationError : ValidationError {
    data object InvalidId : VehicleValidationError
    data object InvalidMaxCapacityKg : VehicleValidationError
    data object InvalidCostPerKm : VehicleValidationError
    data object InvalidCurrentHubId : VehicleValidationError
    data object NoFieldsProvided : VehicleValidationError
}