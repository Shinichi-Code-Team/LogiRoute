package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.command.CommandHistoryStore

class RedoLogisticsCommandUseCase(
    private val historyStore: CommandHistoryStore
) {
    operator fun invoke(): Boolean =
        historyStore.redoStack.pollFirst()?.let { command ->
            command.execute().also { isSuccess ->
                if (isSuccess) {
                    historyStore.history.push(command)
                }
            }
        } ?: false
}
