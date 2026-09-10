package com.example.logiroute.data.datasource.csv

import com.example.logiroute.data.dataholder.FleetRaw
import com.example.logiroute.data.datasource.VehicleDataSource
import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.data.processing.writer.FleetWriter

class CsvVehicleDataSource(
    private val loader: Loader,
    private val writer: FleetWriter
) : VehicleDataSource {

    override fun getFleets(): List<FleetRaw> {
        return loader.loadFleets()
    }

    override fun saveFleets(fleets: List<FleetRaw>) {
        writer.writeFleet(fleets)
    }
}