package com.example.logiroute.domain.validator

object ValidationRules {

    private val packageIdPattern = Regex("""^PKG-\d{6}$""")
    private val routeIdPattern = Regex("""^RT-\d{5}$""")
    private val vehicleIdPattern = Regex("""^TRK-\d{4}$""")
    private val warehouseIdPattern = Regex("""^WH-\d{3}$""")

    fun validatePackageId(
        value: String?
    ): ValidationError? {
        return validateId(
            value = value,
            pattern = packageIdPattern
        )
    }

    fun validateRouteId(
        value: String?
    ): ValidationError? {
        return validateId(
            value = value,
            pattern = routeIdPattern
        )
    }

    fun validateVehicleId(
        value: String?
    ): ValidationError? {
        return validateId(
            value = value,
            pattern = vehicleIdPattern
        )
    }

    fun validateWarehouseId(
        value: String?,
        field: ValidationField = ValidationField.ID
    ): ValidationError? {
        return validateId(
            value = value,
            pattern = warehouseIdPattern,
            field = field
        )
    }

    fun validateNonBlank(
        value: String?,
        field: ValidationField
    ): ValidationError? {
        return if (value.isNullOrBlank()) {
            ValidationError(
                field = field,
                reason = ValidationReason.REQUIRED
            )
        } else {
            null
        }
    }

    fun validatePositiveDouble(
        value: Double?,
        field: ValidationField
    ): ValidationError? {
        return when {
            value == null -> {
                ValidationError(
                    field = field,
                    reason = ValidationReason.REQUIRED
                )
            }

            value <= 0.0 -> {
                ValidationError(
                    field = field,
                    reason = ValidationReason.MUST_BE_POSITIVE
                )
            }

            else -> null
        }
    }

    fun validateNonNegativeInt(
        value: Int?,
        field: ValidationField
    ): ValidationError? {
        return when {
            value == null -> {
                ValidationError(
                    field = field,
                    reason = ValidationReason.REQUIRED
                )
            }

            value < 0 -> {
                ValidationError(
                    field = field,
                    reason = ValidationReason.MUST_BE_NON_NEGATIVE
                )
            }

            else -> null
        }
    }

    fun validateLatitude(
        value: Double?
    ): ValidationError? {
        return when {
            value == null -> {
                ValidationError(
                    field = ValidationField.LATITUDE,
                    reason = ValidationReason.REQUIRED
                )
            }

            value !in -90.0..90.0 -> {
                ValidationError(
                    field = ValidationField.LATITUDE,
                    reason = ValidationReason.OUT_OF_RANGE
                )
            }

            else -> null
        }
    }

    fun validateLongitude(
        value: Double?
    ): ValidationError? {
        return when {
            value == null -> {
                ValidationError(
                    field = ValidationField.LONGITUDE,
                    reason = ValidationReason.REQUIRED
                )
            }

            value !in -180.0..180.0 -> {
                ValidationError(
                    field = ValidationField.LONGITUDE,
                    reason = ValidationReason.OUT_OF_RANGE
                )
            }

            else -> null
        }
    }

    private fun validateId(
        value: String?,
        pattern: Regex,
        field: ValidationField = ValidationField.ID
    ): ValidationError? {
        return when {
            value.isNullOrBlank() -> {
                ValidationError(
                    field = field,
                    reason = ValidationReason.REQUIRED
                )
            }

            !pattern.matches(value) -> {
                ValidationError(
                    field = field,
                    reason = ValidationReason.INVALID_FORMAT
                )
            }

            else -> null
        }
    }
}