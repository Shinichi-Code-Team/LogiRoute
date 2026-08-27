package com.example.logiroute.data.processing.writer

import com.example.logiroute.data.dataholder.FleetRaw
import java.io.File

class FleetWriter(private val filePath: String) {
    fun writeFleet(fleets: List<FleetRaw>) {
        val file = File(filePath)
        file.printWriter().use { writer ->

            writer.println("vehicleId,currentHubId,maxCapacityKg,costPerKm")

            fleets.forEach { fleet ->
                fleet.vehicleIds.forEach { vehicleId ->
                    writer.println(
                        "$vehicleId," +
                                "${fleet.currentHubId}," +
                                "${fleet.maxCapacityKg}," +
                                "${fleet.costPerKm}"
                    )
                }
            }
        }
    }
}


