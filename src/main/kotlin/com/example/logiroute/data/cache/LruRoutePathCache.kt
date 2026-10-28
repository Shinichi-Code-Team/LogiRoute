package com.example.logiroute.data.cache

import com.example.logiroute.domain.cache.RouteCacheKey
import com.example.logiroute.domain.cache.RouteCacheStatistics
import com.example.logiroute.domain.cache.RoutePathCache
import com.example.logiroute.domain.model.Warehouse
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class LruRoutePathCache(
    private val capacity: Int = 50
) : RoutePathCache {

    private val nodes: MutableMap<RouteCacheKey, Node> = HashMap()
    private val mutex = Mutex()

    private var mostRecent: Node? = null
    private var leastRecent: Node? = null

    private var cacheHits = 0L
    private var cacheMisses = 0L
    private var cacheEvictions = 0L

    init {
        require(capacity > 0) {
            "Cache capacity must be greater than zero."
        }
    }

    override suspend fun get(key: RouteCacheKey): List<Warehouse>? = mutex.withLock {
        val node = nodes[key]

        if (node == null) {
            cacheMisses += 1
            return@withLock null
        }

        moveToMostRecent(node)
        cacheHits += 1
        node.value
    }

    override suspend fun put(
        key: RouteCacheKey,
        path: List<Warehouse>
    ) = mutex.withLock {
        val existingNode = nodes[key]

        if (existingNode != null) {
            existingNode.value = path
            moveToMostRecent(existingNode)
            return@withLock
        }

        val newNode = Node(
            key = key,
            value = path
        )

        nodes[key] = newNode
        addToMostRecent(newNode)

        if (nodes.size > capacity) {
            evictLeastRecent()
        }
    }

    override suspend fun invalidate(key: RouteCacheKey): Unit = mutex.withLock {
        nodes.remove(key)?.let(::unlink)
    }

    override suspend fun statistics(): RouteCacheStatistics = mutex.withLock {
        RouteCacheStatistics(
            hits = cacheHits,
            misses = cacheMisses,
            evictions = cacheEvictions
        )
    }

    private fun moveToMostRecent(node: Node) {
        unlink(node)
        addToMostRecent(node)
    }

    private fun addToMostRecent(node: Node) {
        node.previous = null
        node.next = mostRecent

        mostRecent?.previous = node
        mostRecent = node

        if (leastRecent == null) {
            leastRecent = node
        }
    }

    private fun unlink(node: Node) {
        val previous = node.previous
        val next = node.next

        if (previous == null) {
            mostRecent = next
        } else {
            previous.next = next
        }

        if (next == null) {
            leastRecent = previous
        } else {
            next.previous = previous
        }

        node.previous = null
        node.next = null
    }

    private fun evictLeastRecent() {
        val node = leastRecent ?: return

        unlink(node)
        nodes.remove(node.key)
        cacheEvictions += 1
    }

    private class Node(
        val key: RouteCacheKey,
        var value: List<Warehouse>,
        var previous: Node? = null,
        var next: Node? = null
    )
}
