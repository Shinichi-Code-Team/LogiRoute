package com.example.logiroute.domain.logic.algorithm.routing

import com.example.logiroute.domain.model.Warehouse

class BfsRouter(
    private val warehouses: List<Warehouse>,
    private val pathConstructor: PathConstructor
) : Router {

    var evaluatedNodes = 0
        private set

    override fun findRoute(source: Warehouse, destination: Warehouse): List<Warehouse> {
        val adjacencyMap = buildAdjacencyMap()
        evaluatedNodes = 0

        if (source == destination) {
            return listOf(source)
        }

        if (!adjacencyMap.containsKey(source) || !adjacencyMap.containsKey(destination)) {
            return emptyList()
        }

        val queue = ArrayDeque<Warehouse>()
        val visited = mutableSetOf<Warehouse>()
        val parentMap = mutableMapOf<Warehouse, Warehouse>()

        queue.add(source)
        visited.add(source)

        var isReachable = false

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()
            evaluatedNodes++

            if (current == destination) {
                isReachable = true
                break
            }

            val neighbors = adjacencyMap[current] ?: emptyList()

            for (neighbor in neighbors) {
                if (neighbor !in visited) {
                    visited.add(neighbor)
                    parentMap[neighbor] = current
                    queue.add(neighbor)
                }
            }
        }

        if (!isReachable) {
            return emptyList()
        }

        return pathConstructor.reconstructPath(
            parentMap,
            source,
            destination
        )
    }

    private fun buildAdjacencyMap(): Map<Warehouse, List<Warehouse>> {
        return warehouses.associateWith { warehouse ->
            warehouse.outgoingRoutes.map { it.destination }
        }
    }
}