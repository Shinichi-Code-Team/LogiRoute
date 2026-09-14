package com.example.logiroute.data.csv.datasource.impl

import com.example.logiroute.data.csv.datasource.CsvVehicleDataSource
import com.example.logiroute.data.csv.processing.loader.Loader
import com.example.logiroute.data.csv.processing.writer.FleetWriter
import com.example.logiroute.data.csv.raw.FleetRaw

class FileCsvVehicleDataSource(
    private val loader: Loader,
    private val writer: FleetWriter
) : CsvVehicleDataSource {

    override fun getFleets(): List<FleetRaw> {
        return loader.loadFleets()
    }

    override fun saveFleets(fleets: List<FleetRaw>) {
        writer.writeFleet(fleets)
    }
}