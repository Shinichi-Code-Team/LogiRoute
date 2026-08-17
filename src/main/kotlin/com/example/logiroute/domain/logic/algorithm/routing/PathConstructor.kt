package com.example.logiroute.domain.logic.algorithm.routing

import com.example.logiroute.domain.model.Warehouse

class PathConstructor {

    fun reconstructPath(
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
        path.reverse()
        return path
    }
}