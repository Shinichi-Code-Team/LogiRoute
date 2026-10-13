package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.request.DetectEmergencyCargoRescueRequest
import com.example.logiroute.domain.model.request.RescueOpportunity
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope

class DetectEmergencyCargoRescueOpportunitiesUseCase(
    private val packageRepository: PackageRepository,
    private val vehicleRepository: VehicleRepository,
    private val warehouseRepository: WarehouseRepository,
    private val findOptimalPathUseCase: FindOptimalPathUseCase
) {

    suspend operator fun invoke(
        request: DetectEmergencyCargoRescueRequest
    ): List<RescueOpportunity> {

        val currentWarehouse =
            fetchValidatedWarehouse(request.warehouseId)

        val (packages, vehicles) = coroutineScope {
            val packagesDeferred = async {
                packageRepository.getAllPackages()
            }

            val vehiclesDeferred = async {
                vehicleRepository.getAllVehicles()
            }

            packagesDeferred.await() to vehiclesDeferred.await()
        }

        val urgentPackages =
            validateUrgentPackages(
                packages = packages,
                warehouseId = currentWarehouse.id
            )

        val availableVehicles =
            validateAvailableVehicles(
                vehicles = vehicles,
                warehouseId = currentWarehouse.id
            )

        val opportunities =
            buildRescueOpportunities(
                currentWarehouse,
                urgentPackages,
                availableVehicles
            )

        if (opportunities.isEmpty()) {
            throw LogisticsException.NoSuitableVehicleException(
                currentWarehouse.id
            )
        }

        return opportunities
    }

    private suspend fun fetchValidatedWarehouse(
        warehouseId: String
    ): Warehouse {

        return warehouseRepository.getAllWarehouses()
            .firstOrNull { it.id == warehouseId }
            ?: throw LogisticsException.WarehouseNotFoundException(
                warehouseId
            )
    }

    private fun validateUrgentPackages(
        packages: List<Package>,
        warehouseId: String
    ): List<Package> {

        val urgentPackages = packages.filter {
            it.origin.id == warehouseId &&
                    it.priority == Priority.URGENT
        }

        if (urgentPackages.isEmpty()) {
            throw LogisticsException.NoUrgentPackagesException(
                warehouseId
            )
        }

        return urgentPackages
    }

    private fun validateAvailableVehicles(
        vehicles: List<Vehicle>,
        warehouseId: String
    ): List<Vehicle> {

        val availableVehicles = vehicles.filter {
            it.currentHub.id == warehouseId
        }

        if (availableVehicles.isEmpty()) {
            throw LogisticsException.NoSuitableVehicleException(
                warehouseId
            )
        }

        return availableVehicles
    }

    private suspend fun buildRescueOpportunities(
        currentWarehouse: Warehouse,
        urgentPackages: List<Package>,
        availableVehicles: List<Vehicle>
    ): List<RescueOpportunity> {

        return urgentPackages.flatMap { urgentPackage ->
            findOpportunitiesForPackage(
                currentWarehouse,
                urgentPackage,
                availableVehicles
            )
        }
    }

    private suspend fun findOpportunitiesForPackage(
        currentWarehouse: Warehouse,
        urgentPackage: Package,
        availableVehicles: List<Vehicle>
    ): List<RescueOpportunity> {

        val routePath = findOptimalPathUseCase(
            source = currentWarehouse,
            destination = urgentPackage.destination
        )

        val nextHop = routePath.getOrNull(1)

        return if (nextHop != null) {

            availableVehicles.map { vehicle ->
                RescueOpportunity(
                    urgentPackage = urgentPackage,
                    currentWarehouse = currentWarehouse,
                    nextHopWarehouse = nextHop,
                    availableVehicle = vehicle
                )
            }

        } else {
            emptyList()
        }
    }
}