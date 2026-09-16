package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.dto.route.CreateRouteRequestDto
import com.example.logiroute.data.remote.dto.route.RouteResponseDto
import com.example.logiroute.data.remote.dto.route.UpdateRouteRequestDto

interface RemoteRouteDataSource {

    fun getRoutes(): List<RouteResponseDto>

    fun getRouteById(id: String): RouteResponseDto?

    fun createRoute(request: CreateRouteRequestDto): RouteResponseDto

    fun updateRoute(
        id: String,
        request: UpdateRouteRequestDto
    ): RouteResponseDto

    fun deleteRoute(id: String)
}