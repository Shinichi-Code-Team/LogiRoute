package com.example.logiroute.domain.validator

sealed interface WarehouseValidationError : ValidationError {

    data object InvalidId : WarehouseValidationError

    data object InvalidName : WarehouseValidationError

    data object InvalidRegionalZone : WarehouseValidationError

    data object InvalidLatitude : WarehouseValidationError

    data object InvalidLongitude : WarehouseValidationError

    data object NoFieldsProvided : WarehouseValidationError
}