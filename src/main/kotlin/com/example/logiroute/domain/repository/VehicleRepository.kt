package com.example.logiroute.domain.repository

import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.request.UpdateVehicleInput

interface VehicleRepository {

     suspend fun getAllVehicles(): List<Vehicle>

     suspend fun getVehicleById(id: String): Vehicle?

     suspend fun addVehicle(vehicle: Vehicle): Boolean

     suspend fun updateVehicle(id: String, input: UpdateVehicleInput): Boolean

     suspend fun deleteVehicle(id: String): Boolean
}