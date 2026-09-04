package com.example.logiroute.domain.command

class CommandInvoker {

    private val undoStack = ArrayDeque<Command>()
    private val redoStack = ArrayDeque<Command>()

    fun executeCommand(command: Command) {
        command.execute()
        undoStack.addLast(command)
        redoStack.clear()
    }

    fun undoLast(): Boolean {
        val lastCommand = undoStack.removeLastOrNull() ?: return false

        lastCommand.undo()
        redoStack.addLast(lastCommand)
        return true
    }

    fun redoLast(): Boolean {
        val lastCommand = redoStack.removeLastOrNull() ?: return false
        lastCommand.execute()
        undoStack.addLast(lastCommand)
        return true
    }

    fun undoAll() {
        generateSequence {
            undoStack.removeLastOrNull()
        }.forEach { command ->
            command.undo()
            redoStack.addLast(command)
        }
    }

    fun historySize(): Int = undoStack.size
}