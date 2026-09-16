package com.example.logiroute.domain.repository

import com.example.logiroute.domain.model.Vehicle

interface VehicleRepository {

     suspend fun getAllVehicles(): List<Vehicle>

     suspend fun getVehicleById(id: String): Vehicle?

     suspend fun addVehicle(vehicle: Vehicle): Boolean

     suspend fun updateVehicle(vehicle: Vehicle): Boolean

     suspend fun deleteVehicle(id: String): Boolean
}