package com.example.logiroute.domain.logic.algorithm.routing

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository

class DijkstraRouter(
    private val warehousesRepository: WarehouseRepository,
    private val pathConstructor: PathConstructor,
    private val routeWeight: (Route) -> Double
) : Router {

    private val adjacencyMap = buildWeightedAdjacencyMap()

    override fun findRoute(
        source: Warehouse,
        destination: Warehouse
    ): List<Warehouse> {

        if (source == destination) {
            return listOf(source)
        }

        val state = createInitialState()

        if (source !in state.distances || destination !in state.distances) {
            return emptyList()
        }

        state.distances[source] = 0.0

        runDijkstra(
            destination = destination,
            state = state
        )

        if (destination !in state.parents) {
            return emptyList()
        }

        return pathConstructor.reconstructPath(
            parentMap = state.parents,
            source = source,
            destination = destination
        )
    }

    private fun createInitialState(): DijkstraState {

        val allWarehouses = adjacencyMap
            .flatMap { (warehouse, routes) ->
                listOf(warehouse) +
                        routes.map { route ->
                            route.destination
                        }
            }
            .distinct()

        val distances = allWarehouses
            .associateWith {
                Double.POSITIVE_INFINITY
            }
            .toMutableMap()

        return DijkstraState(
            distances = distances,
            visited = mutableSetOf(),
            parents = mutableMapOf()
        )
    }

    private fun runDijkstra(
        destination: Warehouse,
        state: DijkstraState
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
            state.distances.getValue(current) +
                    routeWeight(route)

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
                .minByOrNull { (_, cost) ->
                    cost
                }
                ?.key
                ?: error("No reachable warehouse found")
        }

    private fun buildWeightedAdjacencyMap():
            Map<Warehouse, List<Route>> {

        return warehousesRepository
            .getAllWarehouses()
            .associateWith { warehouse ->
                warehouse.outgoingRoutes
            }
    }

    private data class DijkstraState(
        val distances: MutableMap<Warehouse, Double>,
        val visited: MutableSet<Warehouse>,
        val parents: MutableMap<Warehouse, Warehouse>
    )
}