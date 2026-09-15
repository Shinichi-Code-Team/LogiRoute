package com.example.logiroute.domain.services.crud.vehicle

import com.example.logiroute.domain.repository.VehicleRepository

class DeleteVehicleUseCase(
    private val vehicleRepository: VehicleRepository
) {

    suspend operator fun invoke(
        id: String
    ): Boolean {

        return vehicleRepository.deleteVehicle(id)
    }
}