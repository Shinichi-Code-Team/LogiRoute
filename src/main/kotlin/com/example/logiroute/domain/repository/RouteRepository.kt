package com.example.logiroute.domain.repository

import com.example.logiroute.domain.model.Route

interface RouteRepository {
    fun getAllRoutes(): List<Route>
}