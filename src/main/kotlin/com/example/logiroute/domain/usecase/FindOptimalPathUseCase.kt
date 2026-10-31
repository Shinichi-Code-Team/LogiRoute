package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.cache.RouteCacheKey
import com.example.logiroute.domain.cache.RouteCostMetric
import com.example.logiroute.domain.cache.RoutePathCache
import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.time.TimeSource
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope


class FindOptimalPathUseCase(
    private val warehousesRepository: WarehouseRepository,
    private val routeRepository: RouteRepository,
    private val routeWeight: (Route) -> Double,
    private val routePathCache: RoutePathCache,
    private val routeCostMetric: RouteCostMetric
) {
    private val metricsMutex = Mutex()
    private var dijkstraExecutions = 0L
    private var dijkstraElapsedNanos = 0L

    suspend fun metricsSnapshot(): DijkstraMetricsSnapshot =
        metricsMutex.withLock {
            DijkstraMetricsSnapshot(
                executions = dijkstraExecutions,
                elapsedNanos = dijkstraElapsedNanos
            )
        }

    private suspend fun recordDijkstra(elapsedNanos: Long) {
        metricsMutex.withLock {
            dijkstraExecutions += 1
            dijkstraElapsedNanos += elapsedNanos
        }
    }

    suspend operator fun invoke(
        source: Warehouse,
        destination: Warehouse
    ): List<Warehouse> {
        val cacheKey = RouteCacheKey(
            sourceWarehouseId = source.id,
            destinationWarehouseId = destination.id,
            costMetric = routeCostMetric
        )

        routePathCache.get(cacheKey)?.let { cachedPath ->
            return cachedPath
        }

        if (source.id == destination.id) {
            return listOf(source).also { path ->
                routePathCache.put(cacheKey, path)
            }
        }

        val adjacencyMap = buildWeightedAdjacencyMap()

        val state = createInitialState(adjacencyMap)

        if (
            source !in state.distances ||
            destination !in state.distances
        ) {
            return emptyList<Warehouse>().also { path ->
                routePathCache.put(cacheKey, path)
            }
        }

        state.distances[source] = 0.0

        val dijkstraTimer = TimeSource.Monotonic.markNow()
        runDijkstra(
            destination = destination,
            state = state,
            adjacencyMap = adjacencyMap
        )
        recordDijkstra(dijkstraTimer.elapsedNow().inWholeNanoseconds)

        val path = reconstructPath(
            parentMap = state.parents,
            source = source,
            destination = destination
        )
        routePathCache.put(cacheKey, path)
        return path
    }

    private suspend fun buildWeightedAdjacencyMap():
            Map<Warehouse, List<Route>> = coroutineScope {

        val warehousesDeferred = async {
            warehousesRepository.getAllWarehouses()
        }

        val routesDeferred = async {
            routeRepository.getAllRoutes()
        }

        val warehousesById = warehousesDeferred
            .await()
            .associateBy { warehouse -> warehouse.id }

        val routes = routesDeferred.await()

        val adjacencyMap = warehousesById.values
            .associateWith { mutableListOf<Route>() }

        for (route in routes) {
            val origin = warehousesById[route.origin.id] ?: continue
            val destination =
                warehousesById[route.destination.id] ?: continue

            val normalizedRoute = route.copy(
                origin = origin,
                destination = destination
            )

            adjacencyMap.getValue(origin).add(normalizedRoute)
        }

        adjacencyMap
    }
    private fun createInitialState(
        adjacencyMap: Map<Warehouse, List<Route>>
    ): DijkstraState {

        val warehouses = adjacencyMap
            .flatMap { (warehouse, routes) ->
                listOf(warehouse) +
                        routes.map { route -> route.destination }
            }
            .distinct()

        return DijkstraState(
            distances = warehouses
                .associateWith { Double.POSITIVE_INFINITY }
                .toMutableMap(),
            visited = mutableSetOf(),
            parents = mutableMapOf()
        )
    }

    private fun runDijkstra(
        destination: Warehouse,
        state: DijkstraState,
        adjacencyMap: Map<Warehouse, List<Route>>
    ) {
        while (hasReachableWarehouse(state)) {
            val current = findLowestCostWarehouse(state)

            if (current == destination) {
                break
            }

            state.visited.add(current)

            adjacencyMap[current]
                .orEmpty()
                .filter { route ->
                    route.destination !in state.visited
                }
                .forEach { route ->
                    updateDistance(
                        current = current,
                        route = route,
                        state = state
                    )
                }
        }
    }

    private fun updateDistance(
        current: Warehouse,
        route: Route,
        state: DijkstraState
    ) {

        val neighbor = route.destination

        val newCost =
            state.distances.getValue(current) + routeWeight(route)

        if (newCost < state.distances.getValue(neighbor)) {
            state.distances[neighbor] = newCost
            state.parents[neighbor] = current
        }
    }

    private fun hasReachableWarehouse(
        state: DijkstraState
    ): Boolean {

        return state.distances.any { (warehouse, cost) ->
            warehouse !in state.visited &&
                    cost < Double.POSITIVE_INFINITY
        }
    }

    private fun findLowestCostWarehouse(
        state: DijkstraState
    ): Warehouse {

        return state.distances
            .filter { (warehouse, _) ->
                warehouse !in state.visited
            }
            .minByOrNull { (_, cost) -> cost }
            ?.key
            ?: error("No reachable warehouse found")
    }

    private fun reconstructPath(
        parentMap: Map<Warehouse, Warehouse>,
        source: Warehouse,
        destination: Warehouse
    ): List<Warehouse> {

        if (destination !in parentMap && source != destination) {
            return emptyList()
        }

        val path = mutableListOf<Warehouse>()
        val visited = mutableSetOf<Warehouse>()
        var current = destination

        while (true) {
            if (!visited.add(current)) {
                return emptyList()
            }

            path.add(current)

            if (current == source) {
                break
            }

            current = parentMap[current] ?: return emptyList()
        }

        return path.reversed()
    }

    private data class DijkstraState(
        val distances: MutableMap<Warehouse, Double>,
        val visited: MutableSet<Warehouse>,
        val parents: MutableMap<Warehouse, Warehouse>
    )
    data class DijkstraMetricsSnapshot(
        val executions: Long,
        val elapsedNanos: Long
    )

}
