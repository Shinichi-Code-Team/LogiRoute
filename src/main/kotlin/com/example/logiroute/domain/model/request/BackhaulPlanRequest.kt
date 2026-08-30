package com.example.logiroute.domain.model.request

import com.example.logiroute.domain.model.Package
import com.example.logiroute.domain.model.Vehicle
import com.example.logiroute.domain.model.Warehouse

data class BackhaulPlanRequest(
    val vehicle: Vehicle,
    val selectedPackages: List<Package>,
    val returnPath: List<Warehouse>
)
