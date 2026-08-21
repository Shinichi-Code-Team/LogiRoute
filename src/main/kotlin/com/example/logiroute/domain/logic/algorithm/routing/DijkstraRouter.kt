package com.example.logiroute.domain.logic.algorithm.routing

import com.example.logiroute.domain.model.Route
import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.WarehouseRepository

class DijkstraRouter(
    private val warehousesRepository: WarehouseRepository,
    private val pathConstructor: PathConstructor
) : Router {
    private val adjacencyMap = buildWeightedAdjacencyMap()

    override fun findRoute(source: Warehouse, destination: Warehouse): List<Warehouse> {
        if (source == destination) {
            return listOf(source)
        }

        val state = createInitialState()

        if (source !in state.distances || destination !in state.distances) {
            return emptyList()
        }

        state.distances[source] = 0.0

        runDijkstra(destination, state)

        if (destination !in state.parents) {
            return emptyList()
        }

        return pathConstructor.reconstructPath(
            state.parents,
            source,
            destination
        )
    }

    private fun createInitialState(): DijkstraState {
        val distances = mutableMapOf<Warehouse, Double>()

        for ((warehouse, routes) in adjacencyMap) {
            distances[warehouse] = Double.POSITIVE_INFINITY

            for (route in routes) {
                distances[route.destination] = Double.POSITIVE_INFINITY
            }
        }

        return DijkstraState(
            distances = distances,
            visited = mutableSetOf(),
            parents = mutableMapOf()
        )
    }

    private fun runDijkstra(destination: Warehouse, state: DijkstraState) {

        while (hasReachableWarehouse(state)) {

            val current = findLowestCostWarehouse(state)

            if (current == destination) {
                break
            }

            state.visited.add(current)

            val routes = adjacencyMap[current] ?: emptyList()

            for (route in routes) {
                val neighbor = route.destination

                if (neighbor !in state.visited) {

                    val newDistance =
                        state.distances.getValue(current) + route.distanceKm

                    if (newDistance < state.distances.getValue(neighbor)) {
                        state.distances[neighbor] = newDistance
                        state.parents[neighbor] = current
                    }
                }
            }
        }
    }

    private fun hasReachableWarehouse(state: DijkstraState): Boolean {
        for ((warehouse, distance) in state.distances) {
            if (
                warehouse !in state.visited &&
                distance < Double.POSITIVE_INFINITY
            ) {
                return true
            }
        }

        return false
    }

    private fun findLowestCostWarehouse(state: DijkstraState): Warehouse {

        var lowestWarehouse = state.distances.keys.first()
        var lowestDistance = Double.POSITIVE_INFINITY

        for ((warehouse, distance) in state.distances) {
            if (
                warehouse !in state.visited &&
                distance < lowestDistance
            ) {
                lowestWarehouse = warehouse
                lowestDistance = distance
            }
        }

        return lowestWarehouse
    }

    private data class DijkstraState(
        val distances: MutableMap<Warehouse, Double>,
        val visited: MutableSet<Warehouse>,
        val parents: MutableMap<Warehouse, Warehouse>
    )

    private fun buildWeightedAdjacencyMap(): Map<Warehouse, List<Route>> {
        val warehouses = warehousesRepository.getAllWarehouses()
        return warehouses.associateWith { warehouse ->
            warehouse.outgoingRoutes
        }
    }

}