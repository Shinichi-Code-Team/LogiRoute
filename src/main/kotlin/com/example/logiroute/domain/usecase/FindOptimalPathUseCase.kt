package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class FindOptimalPathUseCase(
    private val warehousesRepository: WarehouseRepository,
    private val routeRepository: RouteRepository,
    private val routeWeight: (Route) -> Double
) {

    suspend operator fun invoke(
        source: Warehouse,
        destination: Warehouse
    ): List<Warehouse> {

        val adjacencyMap = buildWeightedAdjacencyMap()

        if (source == destination) {
            return listOf(source)
        }

        val state = createInitialState(adjacencyMap)

        if (
            source !in state.distances ||
            destination !in state.distances
        ) {
            return emptyList()
        }

        state.distances[source] = 0.0

        runDijkstra(
            destination = destination,
            state = state,
            adjacencyMap = adjacencyMap
        )

        return reconstructPath(
            parentMap = state.parents,
            source = source,
            destination = destination
        )
    }

    private suspend fun buildWeightedAdjacencyMap():
            Map<Warehouse, List<Route>> {

        val warehousesById = warehousesRepository
            .getAllWarehouses()
            .associateBy { warehouse -> warehouse.id }

        val adjacencyMap = warehousesById.values
            .associateWith { mutableListOf<Route>() }

        for (route in routeRepository.getAllRoutes()) {
            val origin = warehousesById[route.origin.id] ?: continue
            val destination = warehousesById[route.destination.id] ?: continue

            val normalizedRoute = route.copy(
                origin = origin,
                destination = destination
            )

            adjacencyMap.getValue(origin).add(normalizedRoute)
        }

        return adjacencyMap
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
}