package com.example.logiroute.domain.model

class Warehouse (
    val id: String,
    val name: String,
    val regionalZone: String
){
  private val _cargoQueue : MutableList<Package> = mutableListOf()
    val cargoQueue : List<Package> get() = _cargoQueue

    private val _outgoingRoutes : MutableList<Route> = mutableListOf()
  val outgoingRoutes get() = _outgoingRoutes

    internal fun addPackage(pkg: Package){
        _cargoQueue.add(pkg)
    }
  internal fun addRoute(route: Route) {
      _outgoingRoutes.add(route)
  }
}
