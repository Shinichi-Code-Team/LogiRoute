package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.dto.route.CreateRouteRequestDto
import com.example.logiroute.data.remote.dto.route.RouteResponseDto
import com.example.logiroute.data.remote.dto.route.UpdateRouteRequestDto
import com.example.logiroute.data.remote.retry.retryRemote
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest

class SupabaseRouteRemoteDataSource(private val client: SupabaseClient
) : RemoteRouteDataSource {

    private val routeTable =
        client.postgrest.from("routes")

    override suspend fun getRoutes(): List<RouteResponseDto> {
        return retryRemote(operationName = "Route.getRoutes") {
            routeTable.select().decodeList<RouteResponseDto>()
        }
    }

    override suspend fun getRouteById(
        id: String
    ): RouteResponseDto? {
        return retryRemote {
            routeTable
                .select {
                    filter {
                        eq("id", id)
                    }
                }
                .decodeList<RouteResponseDto>()
                .firstOrNull()
        }
    }

    override suspend fun createRoute(
        request: CreateRouteRequestDto
    ): RouteResponseDto {
        return retryRemote {
            routeTable
                .insert(request) {
                    select()
                }
                .decodeSingle<RouteResponseDto>()
        }
    }

    override suspend fun updateRoute(
        id: String,
        request: UpdateRouteRequestDto
    ): RouteResponseDto {
        return retryRemote {
            routeTable
                .update(request) {
                    filter {
                        eq("id", id)
                    }
                    select()
                }
                .decodeSingle<RouteResponseDto>()
        }
    }

    override suspend fun deleteRoute(
        id: String
    ) {
        retryRemote {
            routeTable
                .delete {
                    filter {
                        eq("id", id)
                    }
                }
        }
    }
}