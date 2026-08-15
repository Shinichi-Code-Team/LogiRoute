package com.example.logiroute.domain.logic.algorithm.routing

import com.example.logiroute.domain.model.Warehouse
import java.util.ArrayDeque

class BfsRouter(
    private val adjacencyMap: Map<Warehouse, List<Warehouse>>,
    private val pathConstructor: PathConstructor = PathConstructor()
) : Router {

    override fun findRoute(source: Warehouse, destination: Warehouse): List<Warehouse> {
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
            val current = queue.poll()

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
        return pathConstructor.reconstructPath(parentMap, source, destination)
    }
}