package com.example.logiroute.data.csv.mapper

import com.example.logiroute.data.csv.raw.PackageRaw
import com.example.logiroute.data.csv.raw.PriorityRaw
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.model.Warehouse

class PackageRawMapper {

    fun toDomain(
        raw: PackageRaw,
        origin: Warehouse,
        destination: Warehouse
    ): Package {
        return Package(
            id = raw.id,
            weight = raw.weight,
            origin = origin,
            destination = destination,
            priority = mapPriorityToDomain(raw.priority)
        )
    }

    fun toRaw(packageItem: Package): PackageRaw {
        return PackageRaw(
            id = packageItem.id,
            weight = packageItem.weight,
            originHubId = packageItem.origin.id,
            destinationHubId = packageItem.destination.id,
            priority = mapPriorityToRaw(packageItem.priority)
        )
    }

    private fun mapPriorityToDomain(priority: PriorityRaw): Priority {
        return when (priority) {
            PriorityRaw.LOW -> Priority.LOW
            PriorityRaw.STANDARD -> Priority.STANDARD
            PriorityRaw.URGENT -> Priority.URGENT
        }
    }

    private fun mapPriorityToRaw(priority: Priority): PriorityRaw {
        return when (priority) {
            Priority.LOW -> PriorityRaw.LOW
            Priority.STANDARD -> PriorityRaw.STANDARD
            Priority.URGENT -> PriorityRaw.URGENT
        }
    }
}