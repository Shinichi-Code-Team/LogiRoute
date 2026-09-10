package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.result.VehicleUtilization
import com.example.logiroute.domain.usecase.model.exceptions.LogisticsException

class CalculateVehicleUtilizationUseCase {

    private companion object {
        const val MIN_VALID_CAPACITY_KG = 0.0
        const val PERCENTAGE_MULTIPLIER = 100.0
    }

    operator fun invoke(
        vehicle: Vehicle
    ): VehicleUtilization {
        if (vehicle.maxCapacityKg <= MIN_VALID_CAPACITY_KG) {
            throw LogisticsException.InvalidCapacityException(vehicle.maxCapacityKg)
        }

        val currentLoad = vehicle.loadedPackages
            .sumOf { it.weight }

        val remainingCapacity =
            vehicle.maxCapacityKg - currentLoad

        val utilizationPercentage =
            (currentLoad / vehicle.maxCapacityKg) * PERCENTAGE_MULTIPLIER

        return VehicleUtilization(
            currentLoadKg = currentLoad,
            remainingCapacityKg = remainingCapacity,
            utilizationPercentage = utilizationPercentage
        )
    }
}