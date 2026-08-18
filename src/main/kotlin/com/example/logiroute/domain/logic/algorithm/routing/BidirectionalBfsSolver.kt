package com.example.logiroute.domain.logic.algorithm.routing

import com.example.logiroute.domain.model.Warehouse

data class SearchState(
    val queue: ArrayDeque<Warehouse>,
    val visited: MutableSet<Warehouse>,
    val parentMap: MutableMap<Warehouse, Warehouse?>
)

class BidirectionalBfsSolver {

    fun createState(): SearchState {
        return SearchState(
            queue = ArrayDeque(),
            visited = mutableSetOf(),
            parentMap = mutableMapOf()
        )
    }

    fun hasIntersection(
        forwardState: SearchState,
        backwardState: SearchState
    ): Boolean {
        return forwardState.visited.any {
            it in backwardState.visited
        }
    }
}