package com.example.logiroute.domain.model

data class Warehouse(
    val id: String,
    val name: String,
    val regionalZone: String,
    val latitude: Double,
    val longitude: Double
) {

    private val mutableCargoQueue = mutableListOf<Package>()
    val cargoQueue: List<Package>
        get() = mutableCargoQueue

    private val mutableOutgoingRoutes = mutableListOf<Route>()
    val outgoingRoutes: List<Route>
        get() = mutableOutgoingRoutes

    private val mutableStationedVehicles = mutableListOf<Vehicle>()
    val stationedVehicles: List<Vehicle>
        get() = mutableStationedVehicles

    fun addPackage(packageItem: Package): Boolean {
        if (packageItem.origin !== this)
            return false
        if (mutableCargoQueue.any { it.id == packageItem.id }) {
            return false
        }
        mutableCargoQueue.add(packageItem)
        return true
    }

    fun addOutgoingRoute(route: Route): Boolean {
        if (route.origin !== this) {
            return false
        }

        if (mutableOutgoingRoutes.any { it.routeId == route.routeId }) {
            return false
        }

        mutableOutgoingRoutes.add(route)
        return true
    }

    fun addVehicle(vehicle: Vehicle): Boolean {

        if (vehicle.currentHub !== this) {
            return false
        }

        if (mutableStationedVehicles.any {
                it.vehicleId == vehicle.vehicleId
            }
        ) {
            return false
        }

        mutableStationedVehicles.add(vehicle)
        return true
    }
}
