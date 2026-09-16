package com.example.logiroute.data.remote.datasource.impl

import com.example.logiroute.data.remote.datasource.RemoteRouteDataSource
import com.example.logiroute.data.remote.dto.route.CreateRouteRequestDto
import com.example.logiroute.data.remote.dto.route.RouteResponseDto
import com.example.logiroute.data.remote.dto.route.UpdateRouteRequestDto
import com.example.logiroute.data.remote.provider.SupabaseClientProvider
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest

class SupabaseRouteRemoteDataSource : RemoteRouteDataSource {

    private val routeTable =
        SupabaseClientProvider.client.postgrest.from("routes")

    override suspend fun getRoutes(): List<RouteResponseDto> {
        return routeTable
            .select()
            .decodeList<RouteResponseDto>()
    }

    override suspend fun getRouteById(
        id: String
    ): RouteResponseDto? {
        return routeTable
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeList<RouteResponseDto>()
            .firstOrNull()
    }

    override suspend fun createRoute(
        request: CreateRouteRequestDto
    ): RouteResponseDto {
        return routeTable
            .insert(request) {
                select()
            }
            .decodeSingle<RouteResponseDto>()
    }

    override suspend fun updateRoute(
        id: String,
        request: UpdateRouteRequestDto
    ): RouteResponseDto {
        return routeTable
            .update(request) {
                filter {
                    eq("id", id)
                }
                select()
            }
            .decodeSingle<RouteResponseDto>()
    }

    override suspend fun deleteRoute(
        id: String
    ) {
        routeTable
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }
}