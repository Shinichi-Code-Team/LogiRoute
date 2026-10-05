package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.command.CommandHistoryStore

class UndoLogisticsCommandUseCase(
    private val historyStore: CommandHistoryStore
) {
    operator fun invoke(): Boolean =
        historyStore.history.pollFirst()?.let { command ->
            command.undo().also { isSuccess ->
                if (isSuccess) {
                    historyStore.redoStack.push(command)
                }
            }
        } ?: false
}
