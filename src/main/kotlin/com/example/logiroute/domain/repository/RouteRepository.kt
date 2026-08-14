package com.example.logiroute.domain.repository

import com.example.logiroute.data.dataholder.RouteRaw

interface RouteRepository {
    fun getRoutes(): List<RouteRaw>
}