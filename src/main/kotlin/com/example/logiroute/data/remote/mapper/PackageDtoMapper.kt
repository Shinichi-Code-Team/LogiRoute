package com.example.logiroute.data.remote.mapper

import com.example.logiroute.data.remote.dto.`package`.CreatePackageRequestDto
import com.example.logiroute.data.remote.dto.`package`.PackageResponseDto
import com.example.logiroute.data.remote.dto.`package`.PriorityDto
import com.example.logiroute.data.remote.dto.`package`.UpdatePackageRequestDto
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse

class PackageDtoMapper {

    fun toDomain(
        dto: PackageResponseDto,
        origin: Warehouse,
        destination: Warehouse
    ): Package {
        return Package(
            id = dto.id,
            weight = dto.weight,
            origin = origin,
            destination = destination,
            priority = mapPriorityToDomain(dto.priority)
        )
    }

    fun toCreateRequest(
        packageItem: Package
    ): CreatePackageRequestDto {
        return CreatePackageRequestDto(
            id = packageItem.id,
            weight = packageItem.weight,
            originHubId = packageItem.origin.id,
            destinationHubId = packageItem.destination.id,
            priority = mapPriorityToDto(packageItem.priority)
        )
    }

    fun toUpdateRequest(
        packageItem: Package
    ): UpdatePackageRequestDto {
        return UpdatePackageRequestDto(
            weight = packageItem.weight,
            originHubId = packageItem.origin.id,
            destinationHubId = packageItem.destination.id,
            priority = mapPriorityToDto(packageItem.priority)
        )
    }

    private fun mapPriorityToDomain(
        priority: PriorityDto
    ): Priority {
        return when (priority) {
            PriorityDto.LOW -> Priority.LOW
            PriorityDto.STANDARD -> Priority.STANDARD
            PriorityDto.URGENT -> Priority.URGENT
        }
    }

    private fun mapPriorityToDto(
        priority: Priority
    ): PriorityDto {
        return when (priority) {
            Priority.LOW -> PriorityDto.LOW
            Priority.STANDARD -> PriorityDto.STANDARD
            Priority.URGENT -> PriorityDto.URGENT
        }
    }
}