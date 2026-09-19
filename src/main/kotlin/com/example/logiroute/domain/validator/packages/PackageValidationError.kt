package com.example.logiroute.domain.validator

sealed interface PackageValidationError : ValidationError {
    data object InvalidId : PackageValidationError
    data object InvalidWeight : PackageValidationError
    data object InvalidOriginHubId : PackageValidationError
    data object InvalidDestinationHubId : PackageValidationError
    data object NoFieldsProvided : PackageValidationError
}