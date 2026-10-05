package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.command.LogisticsCommand
import com.example.logiroute.domain.model.command.TreeCommandHistoryStore

class ExecuteBranchingCommandUseCase(
    private val treeStore: TreeCommandHistoryStore
) {
    val historySize: Int
        get() = treeStore.historySize

    operator fun invoke(command: LogisticsCommand): Boolean =
        command.execute().also { isSuccess ->
            if (isSuccess) {
                treeStore.addCommand(command)
            }
        }
}