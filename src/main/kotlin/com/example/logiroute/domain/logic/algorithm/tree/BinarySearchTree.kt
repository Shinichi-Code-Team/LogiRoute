package com.example.logiroute.domain.logic.algorithm.tree

class BinarySearchTree {

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