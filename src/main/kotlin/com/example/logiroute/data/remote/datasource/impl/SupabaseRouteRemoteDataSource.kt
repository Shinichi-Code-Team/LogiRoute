package com.example.logiroute.data.remote.datasource

import com.example.logiroute.data.remote.dto.route.CreateRouteRequestDto
import com.example.logiroute.data.remote.dto.route.RouteResponseDto
import com.example.logiroute.data.remote.dto.route.UpdateRouteRequestDto
import com.example.logiroute.data.remote.ratelimit.RateLimiter
import com.example.logiroute.data.remote.retry.retryRemote
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SupabaseRouteRemoteDataSource(
    private val client: SupabaseClient,
    private val rateLimiter: RateLimiter

) : RemoteRouteDataSource {

    private val routeTable =
        client.postgrest.from("routes")

    override suspend fun getRoutes(): List<RouteResponseDto> = withContext(Dispatchers.IO) {
        retryRemote(rateLimiter) {
            routeTable.select().decodeList<RouteResponseDto>()
        }
    }

    override suspend fun getRouteById(
        id: String
    ): RouteResponseDto? = withContext(Dispatchers.IO) {
        retryRemote (rateLimiter){
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
    ): RouteResponseDto = withContext(Dispatchers.IO) {
        retryRemote(rateLimiter) {
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
    ): RouteResponseDto = withContext(Dispatchers.IO) {
        retryRemote (rateLimiter){
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
        withContext(Dispatchers.IO) {
            retryRemote(rateLimiter) {
                routeTable
                    .delete {
                        filter {
                            eq("id", id)
                        }
                    }
            }
        }
    }
}
