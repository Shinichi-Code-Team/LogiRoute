package com.example.logiroute.domain.validator

sealed interface RouteValidationError : ValidationError {

    data object InvalidId : RouteValidationError

    data object InvalidOriginHubId : RouteValidationError

    data object InvalidDestinationHubId : RouteValidationError

    data object InvalidDistanceKm : RouteValidationError

    data object InvalidTypicalDelayMin : RouteValidationError

    data object NoFieldsProvided : RouteValidationError
}