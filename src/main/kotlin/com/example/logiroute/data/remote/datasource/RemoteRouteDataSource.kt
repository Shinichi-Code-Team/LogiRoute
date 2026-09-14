package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.dto.route.RouteResponseDto

interface RemoteRouteDataSource {
    fun getRoutes(): List<RouteResponseDto>
}