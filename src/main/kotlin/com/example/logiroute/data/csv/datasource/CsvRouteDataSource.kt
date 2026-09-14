package com.example.logiroute.data.csv.datasource

import com.example.logiroute.data.csv.raw.RouteRaw

interface CsvRouteDataSource {
    fun getRoutes(): List<RouteRaw>
}