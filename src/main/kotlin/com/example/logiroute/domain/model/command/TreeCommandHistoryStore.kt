package com.example.logiroute.domain.model.command

class TreeCommandHistoryStore {
    var root: HistoryNode? = null
        private set
    var currentNode: HistoryNode? = null
        private set

    val current: HistoryNode?
        get() = currentNode

    val historySize: Int
        get() {
            var count = 0
            var curr = currentNode
            while (curr != null) {
                count++
                curr = curr.parent
            }
            return count
        }

    fun addCommand(command: LogisticsCommand): HistoryNode {
        val newNode = HistoryNode(command = command, parent = currentNode)
        if (root == null) {
            root = newNode
        } else {
            currentNode?.children?.add(newNode)
        }
        currentNode = newNode
        return newNode
    }

    fun stepBack(): Boolean {
        val currNode = currentNode ?: return false
        val parentNode = currNode.parent ?: return false
        val undone = currNode.command.undo()
        if (undone) {
            currentNode = parentNode
        }
        return undone
    }

    fun stepForward(branchIndex: Int = 0): Boolean {
        val nextNode = currentNode?.children?.getOrNull(branchIndex) ?: return false
        val executed = nextNode.command.execute()
        if (executed) {
            currentNode = nextNode
        }
        return executed
    }

    fun clear() {
        root = null
        currentNode = null
    }
}