package com.example.logiroute.domain.repository

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.request.UpdateRouteInput

interface RouteRepository {

    suspend fun getAllRoutes(): List<Route>

    suspend fun getRouteById(id: String): Route?

    suspend fun createRoute(route: Route): Route

    suspend fun updateRoute(id: String, input: UpdateRouteInput): Route

    suspend fun deleteRoute(id: String)
}