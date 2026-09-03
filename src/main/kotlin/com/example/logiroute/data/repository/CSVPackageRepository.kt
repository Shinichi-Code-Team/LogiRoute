package com.example.logiroute.data.repository

import com.example.logiroute.data.dataholder.PriorityRaw
import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Priority
import com.example.logiroute.domain.repository.PackageRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class CSVPackageRepository(
    private val loader: Loader,
    private val warehouseRepository: WarehouseRepository
) : PackageRepository {

    override fun getAllPackages(): List<Package> {
        val warehouseMap = warehouseRepository
            .getAllWarehouses()
            .associateBy { it.id }

        return loader.loadPackages().mapNotNull { raw ->
            val origin = warehouseMap[raw.originHubId]
            val destination = warehouseMap[raw.destinationHubId]

            if (origin == null || destination == null) {
                null
            } else {
                Package(
                    id = raw.id,
                    weight = raw.weight,
                    origin = origin,
                    destination = destination,
                    priority = mapPriority(raw.priority)
                )
            }
        }
    }

    private fun mapPriority(priorityRaw: PriorityRaw): Priority {
        return when (priorityRaw) {
            PriorityRaw.LOW -> Priority.LOW
            PriorityRaw.STANDARD -> Priority.STANDARD
            PriorityRaw.URGENT -> Priority.URGENT
        }
    }
}