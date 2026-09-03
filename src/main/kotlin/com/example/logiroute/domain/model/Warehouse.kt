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
        if (!canAddPackage(packageItem)) return false

        mutableCargoQueue.add(packageItem)
        return true
    }

    private fun canAddPackage(packageItem: Package): Boolean {
        return packageItem.origin === this &&
                mutableCargoQueue.none { it.id == packageItem.id }
    }

    fun addOutgoingRoute(route: Route): Boolean {
        if (!canAddRoute(route)) return false

        mutableOutgoingRoutes.add(route)
        return true
    }

    private fun canAddRoute(route: Route): Boolean {
        return route.origin === this &&
                mutableOutgoingRoutes.none { it.id == route.id }
    }

    fun addVehicle(vehicle: Vehicle): Boolean {
        if (!canAddVehicle(vehicle)) return false

        mutableStationedVehicles.add(vehicle)
        return true
    }

    private fun canAddVehicle(vehicle: Vehicle): Boolean {
        return vehicle.currentHub === this &&
                mutableStationedVehicles.none { it.id == vehicle.id }
    }
    fun removePackage(packageItem: Package) : Boolean {
        return mutableCargoQueue.remove(packageItem)
    }

    fun restoreCargoQueue(packages: List<Package>) {
        mutableCargoQueue.clear()
        mutableCargoQueue.addAll(packages)
    }
}
