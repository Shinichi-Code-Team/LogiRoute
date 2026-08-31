package com.example.logiroute.domain.repository

import com.example.logiroute.domain.model.Vehicle

interface VehicleRepository {
     fun getAllVehicles(): List<Vehicle>
     fun addVehicle(vehicle: Vehicle): Boolean
}