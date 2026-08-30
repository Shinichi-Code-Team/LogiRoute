package com.example.logiroute.domain.usecase

import com.example.logiroute.com.example.logiroute.domain.usecase.model.exceptions.LogisticsException
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.request.DetectEmergencyCargoRescueRequest
import com.example.logiroute.domain.model.request.RescueOpportunity
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.VehicleRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class DetectEmergencyCargoRescueOpportunitiesUseCase(
    private val packageRepository: PackageRepository,
    private val vehicleRepository: VehicleRepository,
    private val warehouseRepository: WarehouseRepository,
    private val findOptimalPathUseCase: FindOptimalPathUseCase
) {

    operator fun invoke(request: DetectEmergencyCargoRescueRequest): List<RescueOpportunity> {
        val currentWarehouse = warehouseRepository.getAllWarehouses()
            .firstOrNull { it.id == request.warehouseId }
            ?: throw LogisticsException.WarehouseNotFoundException(request.warehouseId)

        val urgentPackages = packageRepository.getAllPackages()
            .filter { it.origin.id == currentWarehouse.id && it.priority == Priority.URGENT }

        if (urgentPackages.isEmpty()) {
            throw LogisticsException.NoUrgentPackagesException(currentWarehouse.id)
        }

        val availableVehicles = vehicleRepository.getAllVehicles()
            .filter { it.currentHub.id == currentWarehouse.id }

        if (availableVehicles.isEmpty()) {
            throw LogisticsException.NoSuitableVehicleException(currentWarehouse.id)
        }

        val opportunities = urgentPackages.flatMap { urgentPackage ->
            val routePath = findOptimalPathUseCase(
                source = currentWarehouse,
                destination = urgentPackage.destination
            )

            val nextHop = routePath.getOrNull(1)

            if (nextHop != null) {
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

        if (opportunities.isEmpty()) {
            throw LogisticsException.NoSuitableVehicleException(currentWarehouse.id)
        }

        return opportunities
    }
}