package com.example.logiroute.domain.usecase

import com.example.logiroute.com.example.logiroute.domain.model.result.TreePerformanceReport

class AnalyzeTreePerformanceUseCase {
    private companion object {
        const val TOTAL_PACKAGE_IDS = 1000
        const val SAMPLE_QUARTER_DIVISOR = 4
        const val SAMPLE_HALF_DIVISOR = 2
        const val SAMPLE_THREE_QUARTERS_MULTIPLIER = 3
    }

    operator fun invoke(): TreePerformanceReport {
        val sequentialIds = generateSequentialPackageIds()

        val unbalancedTree = BinarySearchTree()
        sequentialIds.forEach { id -> unbalancedTree.insert(id) }

        val balancedTree = BalancedBinarySearchTree()
        balancedTree.buildFromSorted(sequentialIds)

        val sampleKeys = sampleAcrossRange(sequentialIds)

        val unbalancedSteps = sampleKeys.associateWith { key ->
            unbalancedTree.searchWithStepCount(key)
        }

        val balancedSteps = sampleKeys.associateWith { key ->
            balancedTree.searchWithStepCount(key)
        }

        return TreePerformanceReport(
            sampleKeys = sampleKeys,
            unbalancedSteps = unbalancedSteps,
            balancedSteps = balancedSteps,
            unbalancedHeight = unbalancedTree.height(),
            balancedHeight = balancedTree.height()
        )
    }

    private fun generateSequentialPackageIds(): List<String> {
        return (1..TOTAL_PACKAGE_IDS).map { index ->
            "PKG-" + index.toString().padStart(6, '0')
        }
    }

    private fun sampleAcrossRange(ids: List<String>): List<String> {
        val sampleIndices = listOf(
            0,
            ids.size / SAMPLE_QUARTER_DIVISOR,
            ids.size / SAMPLE_HALF_DIVISOR,
            (ids.size * SAMPLE_THREE_QUARTERS_MULTIPLIER) / SAMPLE_QUARTER_DIVISOR,
            ids.size - 1
        )
        return sampleIndices.map { index -> ids[index] }
    }
}

private class TreeNode(val key: String) {
    var left: TreeNode? = null
    var right: TreeNode? = null
}

private class BinarySearchTree {
    private var root: TreeNode? = null

    fun insert(key: String) {
        root = insertRecursive(root, key)
    }

    private fun insertRecursive(node: TreeNode?, key: String): TreeNode {
        if (node == null) return TreeNode(key)
        if (key < node.key) {
            node.left = insertRecursive(node.left, key)
        } else {
            node.right = insertRecursive(node.right, key)
        }
        return node
    }

    fun searchWithStepCount(key: String): Int = countSteps(root, key, steps = 1)

    private fun countSteps(node: TreeNode?, key: String, steps: Int): Int {
        if (node == null) return steps - 1
        if (key == node.key) return steps
        return if (key < node.key) {
            countSteps(node.left, key, steps + 1)
        } else {
            countSteps(node.right, key, steps + 1)
        }
    }

    fun height(): Int = heightRecursive(root)

    private fun heightRecursive(node: TreeNode?): Int {
        if (node == null) return 0
        return 1 + maxOf(heightRecursive(node.left), heightRecursive(node.right))
    }
}

private class BalancedBinarySearchTree {
    private var root: TreeNode? = null

    fun buildFromSorted(sortedKeys: List<String>) {
        root = buildBalanced(sortedKeys)
    }

    private fun buildBalanced(sortedKeys: List<String>): TreeNode? {
        if (sortedKeys.isEmpty()) return null
        val midIndex = sortedKeys.size / 2
        val node = TreeNode(sortedKeys[midIndex])
        node.left = buildBalanced(sortedKeys.subList(0, midIndex))
        node.right = buildBalanced(sortedKeys.subList(midIndex + 1, sortedKeys.size))
        return node
    }

    fun searchWithStepCount(key: String): Int = countSteps(root, key, steps = 1)

    private fun countSteps(node: TreeNode?, key: String, steps: Int): Int {
        if (node == null) return steps - 1
        if (key == node.key) return steps
        return if (key < node.key) {
            countSteps(node.left, key, steps + 1)
        } else {
            countSteps(node.right, key, steps + 1)
        }
    }

    fun height(): Int = heightRecursive(root)

    private fun heightRecursive(node: TreeNode?): Int {
        if (node == null) return 0
        return 1 + maxOf(heightRecursive(node.left), heightRecursive(node.right))
    }
}
