package com.example.logiroute.domain.validator

enum class ValidationReason {
    REQUIRED,
    INVALID_FORMAT,
    OUT_OF_RANGE,
    MUST_BE_POSITIVE,
    MUST_BE_NON_NEGATIVE,
    NO_FIELDS_PROVIDED
}