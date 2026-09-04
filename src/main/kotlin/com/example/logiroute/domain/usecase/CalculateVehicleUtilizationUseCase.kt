package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.result.VehicleUtilization
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException

class CalculateVehicleUtilizationUseCase {

    operator fun invoke(
        vehicle: Vehicle
    ): VehicleUtilization {
        if (vehicle.maxCapacityKg <= 0.0) {
            throw LogisticsException.InvalidCapacityException(vehicle.maxCapacityKg)
        }

        val currentLoad = vehicle.loadedPackages
                .sumOf { it.weight }

        val remainingCapacity = vehicle.maxCapacityKg - currentLoad

        val utilizationPercentage = (currentLoad / vehicle.maxCapacityKg) * 100

        return VehicleUtilization(
            currentLoadKg = currentLoad,
            remainingCapacityKg = remainingCapacity,
            utilizationPercentage = utilizationPercentage
        )
    }
}