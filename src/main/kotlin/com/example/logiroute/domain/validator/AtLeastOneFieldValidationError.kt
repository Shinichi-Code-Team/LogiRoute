package com.example.logiroute.domain.validator

sealed interface AtLeastOneFieldValidationError : ValidationError {
    data object NoFieldsProvided : AtLeastOneFieldValidationError
}