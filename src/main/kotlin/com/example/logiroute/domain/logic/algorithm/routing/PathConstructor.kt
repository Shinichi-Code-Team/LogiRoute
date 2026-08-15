package com.example.logiroute.domain.logic.algorithm.routing

import com.example.logiroute.domain.model.Warehouse

class PathConstructor {

    fun reconstructPath(
        parentMap: Map<Warehouse, Warehouse?>,
        destination: Warehouse
    ): List<Warehouse> {
        if (destination !in parentMap) {
            return emptyList()
        }
        val path = mutableListOf<Warehouse>()
        var current: Warehouse? = destination

        while (current != null) {
            path.add(current)
            current = parentMap[current]
        }
        path.reverse()
        return path

    }
}