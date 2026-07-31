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

    fun addPackage(packageItem: Package) {
        mutableCargoQueue.add(packageItem)
    }

    fun addRoute(route: Route) {
        mutableOutgoingRoutes.add(route)
    }

    fun addVehicle(vehicle: Vehicle) {
        mutableStationedVehicles.add(vehicle)
    }
}
