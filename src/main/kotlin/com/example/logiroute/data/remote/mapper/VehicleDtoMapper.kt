package com.example.logiroute.data.remote.mapper

import com.example.logiroute.data.remote.dto.vehicle.CreateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.UpdateVehicleRequestDto
import com.example.logiroute.data.remote.dto.vehicle.VehicleResponseDto
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse

class VehicleDtoMapper {

    fun toDomain(
        dto: VehicleResponseDto,
        warehouse: Warehouse
    ): Vehicle {
        return Vehicle(
            id = dto.id,
            maxCapacityKg = dto.maxCapacityKg,
            costPerKm = dto.costPerKm,
            currentHub = warehouse
        )
    }

    fun toCreateRequest(
        vehicle: Vehicle
    ): CreateVehicleRequestDto {
        return CreateVehicleRequestDto(
            id = vehicle.id,
            currentHubId = vehicle.currentHub.id,
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm
        )
    }

    fun toUpdateRequest(
        vehicle: Vehicle
    ): UpdateVehicleRequestDto {
        return UpdateVehicleRequestDto(
            currentHubId = vehicle.currentHub.id,
            maxCapacityKg = vehicle.maxCapacityKg,
            costPerKm = vehicle.costPerKm
        )
    }
}