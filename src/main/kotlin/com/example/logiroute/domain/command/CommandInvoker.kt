package com.example.logiroute.domain.command

class CommandInvoker {

    private val executedCommands: MutableList<Command> = mutableListOf()

    fun executeCommand(command: Command) {
        command.execute()
        executedCommands.add(command)
    }

    fun undoLast(): Boolean {
        val lastCommand = executedCommands.removeLastOrNull() ?: return false

        lastCommand.undo()
        return true
    }

    fun undoAll() {
        generateSequence {
            executedCommands.removeLastOrNull()
        }.forEach { command ->
            command.undo()
        }
    }

    fun historySize(): Int = executedCommands.size
}