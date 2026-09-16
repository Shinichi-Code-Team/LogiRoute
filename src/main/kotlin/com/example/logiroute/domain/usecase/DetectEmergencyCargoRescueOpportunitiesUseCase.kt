package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.model.request.DetectEmergencyCargoRescueRequest
import com.example.logiroute.domain.model.request.RescueOpportunity
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException

class DetectEmergencyCargoRescueOpportunitiesUseCase(
    private val packageRepository: PackageRepository,
    private val vehicleRepository: VehicleRepository,
    private val warehouseRepository: WarehouseRepository,
    private val findOptimalPathUseCase: FindOptimalPathUseCase
) {

    suspend operator fun invoke(request: DetectEmergencyCargoRescueRequest): List<RescueOpportunity> {
        val currentWarehouse = fetchValidatedWarehouse(request.warehouseId)
        val urgentPackages = fetchValidatedUrgentPackages(currentWarehouse.id)
        val availableVehicles = fetchValidatedAvailableVehicles(currentWarehouse.id)

        val opportunities = buildRescueOpportunities(currentWarehouse, urgentPackages, availableVehicles)

        if (opportunities.isEmpty()) {
            throw LogisticsException.NoSuitableVehicleException(currentWarehouse.id)
        }

        return opportunities
    }

    private fun fetchValidatedWarehouse(warehouseId: String): Warehouse {
        return warehouseRepository.getAllWarehouses()
            .firstOrNull { it.id == warehouseId }
            ?: throw LogisticsException.WarehouseNotFoundException(warehouseId)
    }

     private suspend fun fetchValidatedUrgentPackages(warehouseId: String): List<Package> {
        val packages = packageRepository.getAllPackages()
            .filter { it.origin.id == warehouseId && it.priority == Priority.URGENT }
        if (packages.isEmpty()) {
            throw LogisticsException.NoUrgentPackagesException(warehouseId)
        }
        return packages
    }

     private suspend fun fetchValidatedAvailableVehicles(warehouseId: String): List<Vehicle> {
        val vehicles = vehicleRepository.getAllVehicles()
            .filter { it.currentHub.id == warehouseId }
        if (vehicles.isEmpty()) {
            throw LogisticsException.NoSuitableVehicleException(warehouseId)
        }
        return vehicles
    }

    private fun buildRescueOpportunities(
        currentWarehouse: Warehouse,
        urgentPackages: List<Package>,
        availableVehicles: List<Vehicle>
    ): List<RescueOpportunity> {
        return urgentPackages.flatMap { urgentPackage ->
            findOpportunitiesForPackage(currentWarehouse, urgentPackage, availableVehicles)
        }
    }

    private fun findOpportunitiesForPackage(
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