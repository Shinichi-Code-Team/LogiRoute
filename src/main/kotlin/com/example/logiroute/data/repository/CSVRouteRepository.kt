package com.example.logiroute.data.repository

import com.example.logiroute.data.dataholder.RouteRaw
import com.example.logiroute.data.processing.loader.Loader
import com.example.logiroute.domain.repository.RouteRepository

class CSVRouteRepository(private val loader: Loader) : RouteRepository {
    override fun getRoutes(): List<RouteRaw> {
        return loader.loadRoutes()
    }
}