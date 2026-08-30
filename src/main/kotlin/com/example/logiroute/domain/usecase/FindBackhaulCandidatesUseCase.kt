package com.example.logiroute.com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.RouteRepository

class FindBackhaulCandidatesUseCase(
    private val packageRepository: PackageRepository,
    private val routeRepository: RouteRepository
) {
  operator fun invoke(
      vehicle: Vehicle,
      currentHub: Warehouse
  ): List<Package> {
      val possibleDestinations = routeRepository
          .getAllRoutes()
          .filter { route ->
              route.origin == currentHub
          }
          .map { route ->
              route.destination
          }
          .toSet()

      return packageRepository
          .getAllPackages()
          .filter { pkg ->
              pkg.origin == currentHub &&
                      pkg.destination in possibleDestinations &&
                      pkg.weight <= vehicle.maxCapacityKg
          }
  }
}