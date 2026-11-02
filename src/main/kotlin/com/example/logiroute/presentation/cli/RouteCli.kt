package com.example.logiroute.presentation.cli

import com.example.logiroute.domain.cache.RouteCacheKey
import com.example.logiroute.domain.cache.RouteCostMetric
import com.example.logiroute.domain.cache.RoutePathCache
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.usecase.FindOptimalPathUseCase
import com.example.logiroute.domain.usecase.crud.warehouse.ReadWarehouseUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.TimeSource

class RouteCli(
    private val readWarehouseUseCase: ReadWarehouseUseCase,
    private val findOptimalPathUseCase: FindOptimalPathUseCase,
    private val routePathCache: RoutePathCache
) {
    suspend fun run() {
        while (true) {
            printMenu()
            when (readCliInput("Choose a route command:") ?: "0") {
                "1" -> safelyRunCliCommand { calculateRoute() }
                "2" -> safelyRunCliCommand { benchmarkRouteCache() }
                "0" -> return
                else -> println("Unknown option. Choose one of the listed commands.")
            }
        }
    }

    private fun printMenu() {
        println()
        println("=== Route CLI ===")
        println("1. Calculate optimal route")
        println("2. Benchmark LRU cache (1,000 repeated lookups)")
        println("0. Back to main menu")
    }

    private suspend fun calculateRoute() {
        val origin = readWarehouse("Origin") ?: return
        val destination = readWarehouse("Destination") ?: return
        val route = withLoadingIndicator("Calculating route") {
            findOptimalPathUseCase(origin, destination)
        }

        if (route.isEmpty()) {
            println("No route connects those warehouses.")
            return
        }

        println(route.joinToString(" -> ") { warehouse ->
            "${warehouse.name} (${warehouse.id})"
        })
    }

    private suspend fun benchmarkRouteCache() {
        val origin = readWarehouse("Origin") ?: return
        val destination = readWarehouse("Destination") ?: return

        if (origin.id == destination.id) {
            println("Choose two different warehouses for the cache benchmark.")
            return
        }

        val key = RouteCacheKey(
            sourceWarehouseId = origin.id,
            destinationWarehouseId = destination.id,
            costMetric = RouteCostMetric.DISTANCE
        )
        routePathCache.invalidate(key)

        val initialCacheStats = routePathCache.statistics()
        val initialSearchStats = findOptimalPathUseCase.metricsSnapshot()
        val coldTimer = TimeSource.Monotonic.markNow()
        findOptimalPathUseCase(origin, destination)
        val coldElapsedNanos = coldTimer.elapsedNow().inWholeNanoseconds
        val coldCacheStats = routePathCache.statistics()
        val coldSearchStats = findOptimalPathUseCase.metricsSnapshot()

        val repetitions = 1_000
        val warmTimer = TimeSource.Monotonic.markNow()
        repeat(repetitions) {
            findOptimalPathUseCase(origin, destination)
        }
        val warmElapsedNanos = warmTimer.elapsedNow().inWholeNanoseconds
        val finalCacheStats = routePathCache.statistics()
        val finalSearchStats = findOptimalPathUseCase.metricsSnapshot()

        val cacheHits = finalCacheStats.hits - coldCacheStats.hits
        val repeatedDijkstraRuns =
            finalSearchStats.executions - coldSearchStats.executions
        val coldDijkstraNanos =
            coldSearchStats.elapsedNanos - initialSearchStats.elapsedNanos
        val estimatedSavedNanos =
            (coldDijkstraNanos * repetitions - warmElapsedNanos).coerceAtLeast(0L)

        println("LRU route cache benchmark")
        println("Cold lookup: ${coldElapsedNanos / 1_000_000} ms")
        println("Cold lookup Dijkstra runs: ${coldSearchStats.executions - initialSearchStats.executions}")
        println("Repeated lookups: $repetitions")
        println("Cache hits: $cacheHits")
        println("Dijkstra runs during repeated lookups: $repeatedDijkstraRuns")
        println("Repeated lookup elapsed: ${warmElapsedNanos / 1_000_000} ms")
        println("Estimated Dijkstra time saved: ${estimatedSavedNanos / 1_000_000} ms")
        println("Cold lookup cache misses: ${coldCacheStats.misses - initialCacheStats.misses}")
    }

    private suspend fun readWarehouse(label: String): Warehouse? {
        val id = readCliInput("$label warehouse ID:") ?: return null
        return readWarehouseUseCase(id).fold(
            onSuccess = { warehouse ->
                warehouse ?: run {
                    println("$label warehouse $id was not found.")
                    null
                }
            },
            onFailure = { error ->
                println("Could not load $label warehouse: ${error.message}")
                null
            }
        )
    }

    private suspend fun <T> withLoadingIndicator(
        message: String,
        action: suspend () -> T
    ): T = coroutineScope {
        val indicator = launch {
            val frames = listOf("|", "/", "-", "\\")
            var frameIndex = 0
            while (isActive) {
                print("\r$message ${frames[frameIndex % frames.size]}")
                frameIndex += 1
                delay(120L)
            }
        }

        val result = try {
            action()
        } finally {
            withContext(NonCancellable + Dispatchers.IO) {
                indicator.cancelAndJoin()
            }
            print("\r")
        }
        println("$message completed.")
        result
    }
}
