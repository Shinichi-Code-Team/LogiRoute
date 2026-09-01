package com.example.logiroute.domain.usecase.model.exceptions

open class LogisticsException(message: String) : Exception(message) {

    companion object {
        const val INVALID_CAPACITY_EXCEPTION = "Capacity threshold must be greater than zero. Provided:"
        const val ZERO_FLEET_CAPACITY_EXCEPTION = "Total fleet capacity in the warehouse cannot be zero."
        const val NO_URGENT_PACKAGES_EXCEPTION = "No URGENT packages found in warehouse with ID:"
        const val NO_SUITABLE_VEHICLE_EXCEPTION = "No available vehicles found for transit in warehouse with ID:"
        const val WAREHOUSE_NOT_FOUND_EXCEPTION = "Warehouse not found with ID:"
        const val ROOT_HUB_NOT_FOUND_EXCEPTION = "Global hub root was not found."
        const val INVALID_HUB_HIERARCHY_EXCEPTION = "Invalid hub hierarchy:"    }

    class InvalidCapacityException(message: Double) :
        LogisticsException("$INVALID_CAPACITY_EXCEPTION $message")

    class ZeroFleetCapacityException(message: String) :
        LogisticsException("$ZERO_FLEET_CAPACITY_EXCEPTION Warehouse ID: $message")

    class NoUrgentPackagesException(message: String) :
        LogisticsException("$NO_URGENT_PACKAGES_EXCEPTION $message")

    class NoSuitableVehicleException(message: String) :
        LogisticsException("$NO_SUITABLE_VEHICLE_EXCEPTION $message")

    class WarehouseNotFoundException(message: String) :
        LogisticsException("$WAREHOUSE_NOT_FOUND_EXCEPTION $message")
    class RootHubNotFoundException :
        LogisticsException(ROOT_HUB_NOT_FOUND_EXCEPTION)

    class InvalidHubHierarchyException(message: String) :
        LogisticsException("$INVALID_HUB_HIERARCHY_EXCEPTION $message")
}
