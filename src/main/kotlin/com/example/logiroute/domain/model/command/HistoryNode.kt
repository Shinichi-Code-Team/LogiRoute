package com.example.logiroute.domain.model.command

class HistoryNode(
    val command: LogisticsCommand,
    val parent: HistoryNode? = null,
    val children: MutableList<HistoryNode> = mutableListOf()
)