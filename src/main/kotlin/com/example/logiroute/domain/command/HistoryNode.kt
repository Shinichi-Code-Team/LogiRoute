package com.example.logiroute.domain.command

data class HistoryNode(
    val command: Command?,
    val parent: HistoryNode?,
    val children: MutableList<HistoryNode> = mutableListOf()
)
