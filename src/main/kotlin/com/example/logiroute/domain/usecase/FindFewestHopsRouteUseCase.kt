package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.WarehouseRepository

class FindFewestHopsRouteUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(
        source: Warehouse,
        destination: Warehouse
    ): List<Warehouse> {

        val adjacencyMap = buildAdjacencyMap()

        if (source == destination) {
            return listOf(source)
        }

        if (
            source !in adjacencyMap ||
            destination !in adjacencyMap
        ) {
            return emptyList()
        }

        val parents = findParents(
            source = source,
            destination = destination,
            adjacencyMap = adjacencyMap
        )

        return reconstructPath(
            parents = parents,
            source = source,
            destination = destination
        )
    }

    private suspend fun buildAdjacencyMap():
            Map<Warehouse, List<Warehouse>> {

        val warehousesById = warehouseRepository
            .getAllWarehouses()
            .associateBy { warehouse -> warehouse.id }

        val adjacencyMap = warehousesById.values
            .associateWith { mutableListOf<Warehouse>() }

        for (route in routeRepository.getAllRoutes()) {
            val origin = warehousesById[route.origin.id] ?: continue
            val destination = warehousesById[route.destination.id] ?: continue

            adjacencyMap.getValue(origin).add(destination)
        }

        return adjacencyMap
    }

    private fun findParents(
        source: Warehouse,
        destination: Warehouse,
        adjacencyMap: Map<Warehouse, List<Warehouse>>
    ): Map<Warehouse, Warehouse> {

        val queue = ArrayDeque<Warehouse>()
        val visited = mutableSetOf<Warehouse>()
        val parents = mutableMapOf<Warehouse, Warehouse>()

        queue.addLast(source)
        visited.add(source)

        while (queue.isNotEmpty()) {
            val current = queue.removeFirst()

            if (current == destination) {
                break
            }

            for (neighbor in adjacencyMap[current].orEmpty()) {
                if (visited.add(neighbor)) {
                    parents[neighbor] = current
                    queue.addLast(neighbor)
                }
            }
        }

        return parents
    }

    private fun reconstructPath(
        parents: Map<Warehouse, Warehouse>,
        source: Warehouse,
        destination: Warehouse
    ): List<Warehouse> {

        if (destination !in parents && source != destination) {
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
                return path.reversed()
            }

            current = parents[current] ?: return emptyList()
        }
    }
}