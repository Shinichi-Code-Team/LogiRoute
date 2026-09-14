package com.example.logiroute.data.csv.datasource.impl

import com.example.logiroute.data.csv.datasource.CsvRouteDataSource
import com.example.logiroute.data.csv.processing.loader.Loader
import com.example.logiroute.data.csv.raw.RouteRaw

class FileCsvRouteDataSource(
    private val loader: Loader
) : CsvRouteDataSource {

    override fun getRoutes(): List<RouteRaw> {
        return loader.loadRoutes()
    }
}