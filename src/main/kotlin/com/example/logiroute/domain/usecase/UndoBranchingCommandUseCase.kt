package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.command.TreeCommandHistoryStore

class UndoBranchingCommandUseCase(
    private val treeStore: TreeCommandHistoryStore
) {
    operator fun invoke(): Boolean = treeStore.stepBack()
}
