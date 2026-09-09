package com.example.logiroute.domain.usecase.command

data class HistoryNode(
    val command: Command?,
    val parent: HistoryNode?,
    val children: MutableList<HistoryNode> = mutableListOf()
)
