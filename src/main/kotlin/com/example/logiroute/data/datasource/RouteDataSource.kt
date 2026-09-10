package com.example.logiroute.data.datasource

import com.example.logiroute.data.dataholder.RouteRaw

interface RouteDataSource {
    fun getRoutes(): List<RouteRaw>
}