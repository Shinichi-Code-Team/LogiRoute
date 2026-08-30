package com.example.logiroute.com.example.logiroute.domain.usecase.model.exceptions

open class LogisticsException(message: String) : Exception(message) {
    companion object {
        const val Invalid_Capacity_Exception = "Capacity threshold must be greater than zero. Provided:"
    }

    class InvalidCapacityException(message: String) :
        LogisticsException(Invalid_Capacity_Exception)

    class ZeroFleetCapacityException(message: String)
        : LogisticsException(message)

    class NoUrgentPackagesException(warehouseId: String) :
        LogisticsException("No URGENT packages found in warehouse with ID: $warehouseId")

    class NoSuitableVehicleException(warehouseId: String) :
        LogisticsException("No available vehicles found for transit in warehouse with ID: $warehouseId")

    class WarehouseNotFoundException(warehouseId: String) :
        LogisticsException("Warehouse not found with ID: $warehouseId")

}