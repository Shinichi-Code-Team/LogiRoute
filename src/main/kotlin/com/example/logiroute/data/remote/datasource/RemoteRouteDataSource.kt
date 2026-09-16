package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.dto.route.CreateRouteRequestDto
import com.example.logiroute.data.remote.dto.route.RouteResponseDto
import com.example.logiroute.data.remote.dto.route.UpdateRouteRequestDto

interface RemoteRouteDataSource {

    suspend fun getRoutes(): List<RouteResponseDto>

    suspend fun getRouteById(id: String): RouteResponseDto?

    suspend fun createRoute(
        request: CreateRouteRequestDto
    ): RouteResponseDto

    suspend fun updateRoute(
        id: String,
        request: UpdateRouteRequestDto
    ): RouteResponseDto

    suspend fun deleteRoute(id: String)
}