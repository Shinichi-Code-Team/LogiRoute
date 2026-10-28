package com.example.logiroute.domain.cache

import com.example.logiroute.domain.model.Warehouse

data class RouteCacheKey(
    val sourceWarehouseId: String,
    val destinationWarehouseId: String,
    val costMetric: RouteCostMetric
)

enum class RouteCostMetric {
    DISTANCE,
    DELAY
}

data class RouteCacheStatistics(
    val hits: Long,
    val misses: Long,
    val evictions: Long
)

interface RoutePathCache {
    suspend fun get(key: RouteCacheKey): List<Warehouse>?

    suspend fun put(key: RouteCacheKey, path: List<Warehouse>)

    suspend fun invalidate(key: RouteCacheKey)

    suspend fun statistics(): RouteCacheStatistics
}
