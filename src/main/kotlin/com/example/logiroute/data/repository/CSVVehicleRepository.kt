package com.example.logiroute.data.repository

import com.example.logiroute.data.dataholder.FleetRaw
import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.domain.repository.VehicleRepository

class CSVVehicleRepository(private val loader: Loader) : VehicleRepository {
    override fun getVehicles(): List<FleetRaw> {
        return loader.loadFleets()
    }
}