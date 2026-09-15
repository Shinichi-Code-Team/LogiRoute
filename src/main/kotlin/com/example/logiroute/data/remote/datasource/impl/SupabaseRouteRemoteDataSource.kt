package com.example.logiroute.data.remote.datasource.impl

import com.example.logiroute.data.remote.datasource.RemoteRouteDataSource
import com.example.logiroute.data.remote.dto.route.RouteResponseDto

class SupabaseRouteRemoteDataSource : RemoteRouteDataSource {

    override fun getRoutes(): List<RouteResponseDto> {
        TODO("Connect to Supabase and fetch routes")
    }
}