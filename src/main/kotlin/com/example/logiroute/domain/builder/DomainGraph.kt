package com.example.logiroute.domain.builder

import com.example.logiroute.data.dataholder.*
import com.example.logiroute.domain.model.*

data class DomainGraph(
    val warehouses: List<Warehouse>,
    val packages: List<Package>,
    val routes: List<Route>,
    val vehicles: List<Vehicle>,
)
