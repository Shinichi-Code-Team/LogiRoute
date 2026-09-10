package com.example.logiroute.data.datasource.csv

import com.example.logiroute.data.dataholder.RouteRaw
import com.example.logiroute.data.datasource.RouteDataSource
import com.example.logiroute.data.processing.loader.Loader

class CsvRouteDataSource(
    private val loader: Loader
) : RouteDataSource {

    override fun getRoutes(): List<RouteRaw> {
        return loader.loadRoutes()
    }
}