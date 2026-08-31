package com.example.logiroute.domain.model.exceptions

open class LogisticsException(
    message: String
) : Exception(message) {

    companion object {

        const val INVALID_CAPACITY =
            "Capacity threshold must be greater than zero."

        const val NO_SUITABLE_VEHICLE =
            "No suitable vehicle found."

        const val ROUTE_NOT_FOUND =
            "No route found."

        const val ROUTE_SEGMENT_NOT_FOUND =
            "Route segment not found."
    }

    class InvalidCapacityException(
        message: String
    ) : LogisticsException(message)

    class ZeroFleetCapacityException(
        message: String
    ) : LogisticsException(message)

    class NoSuitableVehicleException(
        message: String
    ) : LogisticsException(message)

    class RouteNotFoundException(
        message: String
    ) : LogisticsException(message)

    class RouteSegmentNotFoundException(
        message: String
    ) : LogisticsException(message)
}