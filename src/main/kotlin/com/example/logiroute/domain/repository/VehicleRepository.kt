package com.example.logiroute.domain.repository

import com.example.logiroute.data.dataholder.FleetRaw
import com.example.logiroute.data.dataholder.PackageRaw

interface VehicleRepository {
     fun getVehicles(): List<FleetRaw>
}