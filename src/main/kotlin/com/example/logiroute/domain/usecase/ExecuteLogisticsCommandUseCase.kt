package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.command.CommandHistoryStore
import com.example.logiroute.domain.model.command.LogisticsCommand

class ExecuteLogisticsCommandUseCase(
    private val historyStore: CommandHistoryStore
) {
    val historySize: Int
        get() = historyStore.history.size

    operator fun invoke(command: LogisticsCommand): Boolean =
        command.execute().also { isSuccess ->
            if (isSuccess) {
                historyStore.history.push(command)
                historyStore.redoStack.clear()
            }
        }
}
