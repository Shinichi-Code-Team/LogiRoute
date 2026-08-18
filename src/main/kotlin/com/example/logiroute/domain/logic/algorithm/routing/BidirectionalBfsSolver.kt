package com.example.logiroute.domain.logic.algorithm.routing

import com.example.logiroute.domain.model.Warehouse


class BidirectionalBfsSolver(
    private val forwardAdjacencyMap: Map<Warehouse, List<Warehouse>>,
    private val backwardAdjacencyMap: Map<Warehouse, List<Warehouse>>
) : Router {
    override fun findRoute(source: Warehouse, destination: Warehouse): List<Warehouse> {
        if (source == destination) return listOf(source)
        if (source !in forwardAdjacencyMap || destination !in backwardAdjacencyMap) return emptyList()

        val forwardState = createState()
        val backwardState = createState()
        forwardState.queue.addLast(source)
        forwardState.visited.add(source)
        backwardState.queue.addLast(destination)
        backwardState.visited.add(destination)

        while (forwardState.queue.isNotEmpty() && backwardState.queue.isNotEmpty()) {
            expandFrontier(forwardState, forwardAdjacencyMap)
            if (hasIntersection(forwardState, backwardState)) break
            expandFrontier(backwardState, backwardAdjacencyMap)
            if (hasIntersection(forwardState, backwardState)) break
        }
        return emptyList()
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