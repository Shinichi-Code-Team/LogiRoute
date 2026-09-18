package com.example.logiroute.domain.validator

sealed interface WarehouseUpdateValidationError : ValidationError {

    data class InvalidId(
        val error: IdValidationError
    ) : WarehouseUpdateValidationError

    data class InvalidName(
        val error: NonBlankValidationError
    ) : WarehouseUpdateValidationError

    data class InvalidRegionalZone(
        val error: NonBlankValidationError
    ) : WarehouseUpdateValidationError

    data class InvalidLatitude(
        val error: LatitudeValidationError
    ) : WarehouseUpdateValidationError

    data class InvalidLongitude(
        val error: LongitudeValidationError
    ) : WarehouseUpdateValidationError

    data object NoFieldsProvided : WarehouseUpdateValidationError
}