package com.example.logiroute.domain.logic.algorithm.tree

class  BalancedBinarySearchTree {

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

    fun searchWithStepCount(key: String): Int {
        return countSteps(root, key, steps = 1)
    }

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