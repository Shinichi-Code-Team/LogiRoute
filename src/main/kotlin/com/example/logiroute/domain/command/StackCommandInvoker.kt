package com.example.logiroute.domain.command

class StackCommandInvoker : CommandInvoker {

    private val undoStack = ArrayDeque<Command>()
    private val redoStack = ArrayDeque<Command>()

    override fun executeCommand(command: Command) {
        command.execute()

        undoStack.addLast(command)

        redoStack.clear()
    }

    override fun undo(): Boolean {
        val lastCommand =
            undoStack.removeLastOrNull()
                ?: return false

        lastCommand.undo()

        redoStack.addLast(lastCommand)

        return true
    }

    override fun redo(): Boolean {
        val lastCommand =
            redoStack.removeLastOrNull()
                ?: return false

        lastCommand.execute()

        undoStack.addLast(lastCommand)

        return true
    }

    override fun historySize(): Int {
        return undoStack.size
    }
}