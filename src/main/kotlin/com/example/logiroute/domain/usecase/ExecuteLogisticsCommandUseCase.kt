package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.command.LogisticsCommand

class ExecuteLogisticsCommandUseCase {
    private val undoStack = ArrayDeque<LogisticsCommand>()
    private val redoStack = ArrayDeque<LogisticsCommand>()

    operator fun invoke(command: LogisticsCommand): Boolean =
        command.execute().also { isSuccess ->
            if (isSuccess) {
                undoStack.addLast(command)
                redoStack.clear()
            }
        }

    fun historySize(): Int = undoStack.size

    internal fun popUndoCommand(): LogisticsCommand? = undoStack.removeLastOrNull()

    internal fun pushRedoCommand(command: LogisticsCommand) {
        redoStack.addLast(command)
    }

    internal fun popRedoCommand(): LogisticsCommand? = redoStack.removeLastOrNull()

    internal fun pushUndoCommand(command: LogisticsCommand) {
        undoStack.addLast(command)
    }
}
