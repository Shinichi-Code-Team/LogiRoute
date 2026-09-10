package com.example.logiroute.data.datasource

import com.example.logiroute.data.dataholder.FleetRaw

interface VehicleDataSource {
    fun getFleets(): List<FleetRaw>
    fun saveFleets(fleets: List<FleetRaw>)
}