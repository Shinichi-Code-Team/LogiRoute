package com.example.logiroute.domain.logic.algorithm.routing

import com.example.logiroute.domain.model.Warehouse


class BidirectionalBfsSolver(
    private val forwardAdjacencyMap: Map<Warehouse, List<Warehouse>>,
    private val backwardAdjacencyMap: Map<Warehouse, List<Warehouse>>
) : Router {
    var lastEvaluatedNodesCount: Int = 0
        private set
    override fun findRoute(source: Warehouse, destination: Warehouse): List<Warehouse> {
        lastEvaluatedNodesCount = 0
        if (source == destination) {
            lastEvaluatedNodesCount = 1
            return listOf(source)
        }
        if (source !in forwardAdjacencyMap || destination !in backwardAdjacencyMap) {
            return emptyList()
        }
        val forwardState = createState()
        val backwardState = createState()
        forwardState.queue.addLast(source)
        forwardState.visited.add(source)
        backwardState.queue.addLast(destination)
        backwardState.visited.add(destination)
        var meetingPoint: Warehouse? = null
        while (forwardState.queue.isNotEmpty() && backwardState.queue.isNotEmpty()) {
            expandFrontier(forwardState, forwardAdjacencyMap)
            meetingPoint = findIntersection(forwardState, backwardState)
            if (meetingPoint != null) break

            expandFrontier(backwardState, backwardAdjacencyMap)
            meetingPoint = findIntersection(forwardState, backwardState)
            if (meetingPoint != null) break
        }
        lastEvaluatedNodesCount = forwardState.visited.size + backwardState.visited.size
        if (meetingPoint == null) return emptyList()
        return reconstructUnifiedPath(
            source = source,
            destination = destination,
            meetingPoint = meetingPoint,
            forwardParentMap = forwardState.parentMap,
            backwardParentMap = backwardState.parentMap
        )
    }

    private fun expandFrontier(
        state: SearchState,
        adjacencyMap: Map<Warehouse, List<Warehouse>>
    ) {
        val levelSize = state.queue.size
        var processed = 0
        while (processed < levelSize) {
            val current = state.queue.removeFirst()
            val neighbors = adjacencyMap[current] ?: emptyList()
            for (neighbor in neighbors) {
                if (neighbor !in state.visited) {
                    state.visited.add(neighbor)
                    state.parentMap[neighbor] = current
                    state.queue.addLast(neighbor)
                }
            }
            processed++
        }
    }
    private fun findIntersection(
        forwardState: SearchState,
        backwardState: SearchState
    ): Warehouse? {
        return forwardState.visited.firstOrNull { it in backwardState.visited }
    }
    private fun reconstructUnifiedPath(
        source: Warehouse,
        destination: Warehouse,
        meetingPoint: Warehouse,
        forwardParentMap: Map<Warehouse, Warehouse>,
        backwardParentMap: Map<Warehouse, Warehouse>
    ): List<Warehouse> {
        val forwardPath = mutableListOf<Warehouse>()
        var current: Warehouse? = meetingPoint
        while (current != null) {
            forwardPath.add(current)
            current = if (current == source) null else forwardParentMap[current]
        }
        forwardPath.reverse()
        val backwardPath = mutableListOf<Warehouse>()
        current = backwardParentMap[meetingPoint]
        while (current != null) {
            backwardPath.add(current)
            current = if (current == destination) null else backwardParentMap[current]
        }
        return forwardPath + backwardPath
    }

    private fun createState(): SearchState {
        return SearchState(
            queue = ArrayDeque(),
            visited = mutableSetOf(),
            parentMap = mutableMapOf()
        )
    }

    private fun hasIntersection(
        forwardState: SearchState,
        backwardState: SearchState
    ): Boolean {
        return forwardState.visited.any {
            it in backwardState.visited
        }
    }

    private data class SearchState(
        val queue: ArrayDeque<Warehouse>,
        val visited: MutableSet<Warehouse>,
        val parentMap: MutableMap<Warehouse, Warehouse>
    )
}