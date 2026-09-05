package com.example.logiroute.domain.command

class TreeCommandInvoker : CommandInvoker {

    private val root = HistoryNode(
        command = null,
        parent = null
    )

    private var currentNode: HistoryNode = root

    override fun executeCommand(command: Command) {
        command.execute()

        val newNode = HistoryNode(
            command = command,
            parent = currentNode
        )

        currentNode.children.add(newNode)

        currentNode = newNode
    }

    override fun undo(): Boolean {
        if (currentNode === root) {
            return false
        }

        currentNode.command?.undo()

        currentNode = currentNode.parent
            ?: return false

        return true
    }

    override fun redo(): Boolean {
        val nextNode =
            currentNode.children.lastOrNull()
                ?: return false

        nextNode.command?.execute()

        currentNode = nextNode

        return true
    }

    fun redo(branchIndex: Int): Boolean {
        val nextNode =
            currentNode.children.getOrNull(branchIndex)
                ?: return false

        nextNode.command?.execute()

        currentNode = nextNode

        return true
    }

    fun branchCount(): Int {
        return currentNode.children.size
    }

    override fun historySize(): Int {
        var size = 0
        var node: HistoryNode? = currentNode

        while (node?.parent != null) {
            size++
            node = node.parent
        }

        return size
    }
}