package com.example.logiroute.domain.logic.algorithm.routing

import com.example.logiroute.domain.model.Warehouse

class BidirectionalBfsRouter(
    private val warehouses: List<Warehouse>,
) : Router {
    val forwardAdjacencyMap = buildForwardAdjacencyMap()
    val backwardAdjacencyMap = buildBackwardAdjacencyMap()
    var lastEvaluatedNodesCount: Int = 0
        private set

    override fun findRoute(
        source: Warehouse, destination: Warehouse
    ): List<Warehouse> {

        lastEvaluatedNodesCount = 0

        if (source == destination) {
            lastEvaluatedNodesCount = 1
            return listOf(source)
        }

        if (!isValidSearch(source, destination)) {
            return emptyList()
        }

        val forwardState = createSearchState(source)

        val backwardState = createSearchState(destination)

        val meetingPoint = searchForMeetingPoint(forwardState, backwardState)

        updateEvaluatedNodesCount(
            forwardState,
            backwardState
        )

        return meetingPoint?.let {
            reconstructPath(
                source = source,
                destination = destination,
                meetingPoint = it,
                forwardParentMap = forwardState.parentMap,
                backwardParentMap = backwardState.parentMap
            )
        } ?: emptyList()
    }


    private fun isValidSearch(
        source: Warehouse,
        destination: Warehouse
    ): Boolean {
        return source in forwardAdjacencyMap &&
                destination in backwardAdjacencyMap
    }


    private fun createSearchState(
        start: Warehouse
    ): SearchState {

        val state = SearchState(
            queue = ArrayDeque(),
            visited = mutableSetOf(),
            parentMap = mutableMapOf(),
            distance = mutableMapOf()
        )

        state.queue.addLast(start)
        state.visited.add(start)
        state.distance[start] = 0

        return state
    }


    private fun searchForMeetingPoint(
        forwardState: SearchState,
        backwardState: SearchState
    ): Warehouse? {

        while (
            forwardState.queue.isNotEmpty() &&
            backwardState.queue.isNotEmpty()
        ) {

            expandFrontier(
                state = forwardState,
                adjacencyMap = forwardAdjacencyMap
            )

            findBestIntersection(
                forwardState,
                backwardState
            )?.let {
                return it
            }

            expandFrontier(
                state = backwardState,
                adjacencyMap = backwardAdjacencyMap
            )

            findBestIntersection(
                forwardState,
                backwardState
            )?.let {
                return it
            }
        }

        return null
    }

    private fun expandFrontier(
        state: SearchState,
        adjacencyMap: Map<Warehouse, List<Warehouse>>
    ) {

        val levelSize = state.queue.size

        repeat(levelSize) {

            val current =
                state.queue.removeFirst()

            state.evaluatedNodes++

            val neighbors =
                adjacencyMap[current] ?: emptyList()

            for (neighbor in neighbors) {

                if (neighbor !in state.visited) {

                    state.visited.add(neighbor)

                    state.parentMap[neighbor] =
                        current

                    state.distance[neighbor] =
                        state.distance[current]!! + 1

                    state.queue.addLast(neighbor)
                }
            }
        }
    }


    private fun findBestIntersection(
        forwardState: SearchState,
        backwardState: SearchState
    ): Warehouse? {

        val intersections =
            forwardState.visited.filter {
                it in backwardState.visited
            }

        if (intersections.isEmpty()) {
            return null
        }

        return intersections.minByOrNull { warehouse ->

            val forwardDistance =
                forwardState.distance[warehouse]
                    ?: Int.MAX_VALUE

            val backwardDistance =
                backwardState.distance[warehouse]
                    ?: Int.MAX_VALUE

            forwardDistance + backwardDistance
        }
    }


    private fun updateEvaluatedNodesCount(
        forwardState: SearchState,
        backwardState: SearchState
    ) {

        lastEvaluatedNodesCount =
            forwardState.evaluatedNodes +
                    backwardState.evaluatedNodes
    }


    private fun reconstructPath(
        source: Warehouse,
        destination: Warehouse,
        meetingPoint: Warehouse,
        forwardParentMap: Map<Warehouse, Warehouse>,
        backwardParentMap: Map<Warehouse, Warehouse>
    ): List<Warehouse> {

        val forwardPath =
            buildForwardPath(
                source = source,
                meetingPoint = meetingPoint,
                parentMap = forwardParentMap
            )

        val backwardPath =
            buildBackwardPath(
                destination = destination,
                meetingPoint = meetingPoint,
                parentMap = backwardParentMap
            )

        return forwardPath + backwardPath
    }

    private fun buildForwardPath(
        source: Warehouse,
        meetingPoint: Warehouse,
        parentMap: Map<Warehouse, Warehouse>
    ): List<Warehouse> {

        val path =
            mutableListOf<Warehouse>()

        var current: Warehouse? = meetingPoint

        while (current != null) {

            path.add(current)

            if (current == source) {
                break
            }

            current = parentMap[current]
        }

        path.reverse()

        return path
    }


    private fun buildBackwardPath(
        destination: Warehouse,
        meetingPoint: Warehouse,
        parentMap: Map<Warehouse, Warehouse>
    ): List<Warehouse> {

        val path =
            mutableListOf<Warehouse>()

        var current =
            parentMap[meetingPoint]

        while (current != null) {

            path.add(current)

            if (current == destination) {
                break
            }

            current = parentMap[current]
        }

        return path
    }

    private data class SearchState(
        val queue: ArrayDeque<Warehouse>,
        val visited: MutableSet<Warehouse>,
        val parentMap: MutableMap<Warehouse, Warehouse>,
        val distance: MutableMap<Warehouse, Int>,
        var evaluatedNodes: Int = 0
    )

    private fun buildForwardAdjacencyMap(): Map<Warehouse, List<Warehouse>> {
        return warehouses.associateWith { warehouse ->
            warehouse.outgoingRoutes.map { it.destination }
        }
    }

    private fun buildBackwardAdjacencyMap(): Map<Warehouse, List<Warehouse>> {
        val backwardMap = warehouses.associateWith { mutableListOf<Warehouse>() }
        for (warehouse in warehouses) {
            for (route in warehouse.outgoingRoutes) {
                backwardMap.getValue(route.destination).add(route.origin)
            }

        }
        return backwardMap
    }
}