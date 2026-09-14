package com.example.logiroute.data.csv.datasource

import com.example.logiroute.data.csv.raw.FleetRaw

interface CsvVehicleDataSource {
    fun getFleets(): List<FleetRaw>
    fun saveFleets(fleets: List<FleetRaw>)
}