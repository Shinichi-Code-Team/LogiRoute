package com.example.logiroute.data.remote.mapper

import com.example.logiroute.data.remote.dto.vehicle.CreateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.UpdateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.VehicleResponseDto
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse

class VehicleDtoMapper {

    fun toDomain(dto: VehicleResponseDto, currentHub: Warehouse): Vehicle {
        return Vehicle(
            id = dto.id,
            maxCapacityKg = dto.maxCapacityKg,
            costPerKm = dto.costPerKm,
            currentHub = currentHub
        )
    }

    fun toCreateRequest(vehicle: Vehicle): CreateVehicleRequestDto {
        return CreateVehicleRequestDto(
            id = vehicle.id,
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm,
            currentHubId = vehicle.currentHub.id
        )
    }

    fun toUpdateRequest(vehicle: Vehicle): UpdateVehicleRequestDto {
        return UpdateVehicleRequestDto(
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm,
            currentHubId = vehicle.currentHub.id
        )
    }
}