package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.command.TreeCommandHistoryStore

class RedoBranchingCommandUseCase(
    private val treeStore: TreeCommandHistoryStore
) {
    operator fun invoke(branchIndex: Int = 0): Boolean = treeStore.stepForward(branchIndex)
}
