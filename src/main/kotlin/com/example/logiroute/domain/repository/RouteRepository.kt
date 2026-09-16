package com.example.logiroute.domain.repository

import com.example.logiroute.domain.model.Route

interface RouteRepository {

    suspend fun getAllRoutes(): List<Route>

    suspend fun getRouteById(id: String): Route?

    suspend fun createRoute(route: Route): Route

    suspend fun updateRoute(id: String, route: Route): Route

    suspend fun deleteRoute(id: String)
}