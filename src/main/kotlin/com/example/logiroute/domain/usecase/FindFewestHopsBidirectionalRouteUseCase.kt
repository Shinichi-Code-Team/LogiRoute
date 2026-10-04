package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.Warehouse
import com.example.logiroute.domain.repository.RouteRepository
import com.example.logiroute.domain.repository.WarehouseRepository
import kotlin.collections.getValue

class FindFewestHopsBidirectionalRouteUseCase(
    private val warehouseRepository: WarehouseRepository,
    private val routeRepository: RouteRepository
) {

    suspend operator fun invoke(
        source: Warehouse,
        destination: Warehouse
    ): List<Warehouse> {

        val warehousesById = warehouseRepository
            .getAllWarehouses()
            .associateBy { warehouse -> warehouse.id }

        if (source == destination) {
            return listOf(source)
        }

        if (
            source !in warehousesById.values ||
            destination !in warehousesById.values
        ) {
            return emptyList()
        }

        val forwardMap = warehousesById.values
            .associateWith { mutableListOf<Warehouse>() }

        val backwardMap = warehousesById.values
            .associateWith { mutableListOf<Warehouse>() }

        for (route in routeRepository.getAllRoutes()) {
            val origin = warehousesById[route.origin.id] ?: continue
            val routeDestination =
                warehousesById[route.destination.id] ?: continue

            forwardMap.getValue(origin).add(routeDestination)
            backwardMap.getValue(routeDestination).add(origin)
        }

        val forwardState = createSearchState(source)
        val backwardState = createSearchState(destination)

        val meetingPoint = findMeetingPoint(
            forwardState = forwardState,
            backwardState = backwardState,
            forwardMap = forwardMap,
            backwardMap = backwardMap
        ) ?: return emptyList()

        return reconstructPath(
            source = source,
            destination = destination,
            meetingPoint = meetingPoint,
            forwardParents = forwardState.parents,
            backwardParents = backwardState.parents
        )
    }

    private fun createSearchState(
        start: Warehouse
    ): SearchState {

        return SearchState(
            queue = ArrayDeque<Warehouse>().apply {
                addLast(start)
            },
            distances = mutableMapOf(start to 0),
            parents = mutableMapOf()
        )
    }

    private fun findMeetingPoint(
        forwardState: SearchState,
        backwardState: SearchState,
        forwardMap: Map<Warehouse, List<Warehouse>>,
        backwardMap: Map<Warehouse, List<Warehouse>>
    ): Warehouse? {

        while (
            forwardState.queue.isNotEmpty() &&
            backwardState.queue.isNotEmpty()
        ) {
            expandLevel(
                state = forwardState,
                adjacencyMap = forwardMap
            )

            findBestIntersection(
                forwardState,
                backwardState
            )?.let { return it }

            expandLevel(
                state = backwardState,
                adjacencyMap = backwardMap
            )

            findBestIntersection(
                forwardState,
                backwardState
            )?.let { return it }
        }

        return null
    }

    private fun expandLevel(
        state: SearchState,
        adjacencyMap: Map<Warehouse, List<Warehouse>>
    ) {

        val levelSize = state.queue.size

        repeat(levelSize) {
            val current = state.queue.removeFirst()
            val nextDistance = state.distances.getValue(current) + 1

            for (neighbor in adjacencyMap[current].orEmpty()) {
                if (neighbor !in state.distances) {
                    state.distances[neighbor] = nextDistance
                    state.parents[neighbor] = current
                    state.queue.addLast(neighbor)
                }
            }
        }
    }

    private fun findBestIntersection(
        forwardState: SearchState,
        backwardState: SearchState
    ): Warehouse? {

        return forwardState.distances.keys
            .filter { warehouse ->
                warehouse in backwardState.distances
            }
            .minByOrNull { warehouse ->
                forwardState.distances.getValue(warehouse) +
                        backwardState.distances.getValue(warehouse)
            }
    }

    private fun reconstructPath(
        source: Warehouse,
        destination: Warehouse,
        meetingPoint: Warehouse,
        forwardParents: Map<Warehouse, Warehouse>,
        backwardParents: Map<Warehouse, Warehouse>
    ): List<Warehouse> {

        val firstPart = buildPathToStart(
            start = source,
            end = meetingPoint,
            parents = forwardParents
        )

        val secondPart = buildPathToStart(
            start = destination,
            end = meetingPoint,
            parents = backwardParents
        )

        if (firstPart.isEmpty() || secondPart.isEmpty()) {
            return emptyList()
        }

        return firstPart.reversed() + secondPart.drop(1)
    }

    private fun buildPathToStart(
        start: Warehouse,
        end: Warehouse,
        parents: Map<Warehouse, Warehouse>
    ): List<Warehouse> {

        val path = mutableListOf<Warehouse>()
        val visited = mutableSetOf<Warehouse>()
        var current = end

        while (true) {
            if (!visited.add(current)) {
                return emptyList()
            }

            path.add(current)

            if (current == start) {
                return path
            }

            current = parents[current] ?: return emptyList()
        }
    }

    private data class SearchState(
        val queue: ArrayDeque<Warehouse>,
        val distances: MutableMap<Warehouse, Int>,
        val parents: MutableMap<Warehouse, Warehouse>
    )
}