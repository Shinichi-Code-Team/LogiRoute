package com.example.logiroute.domain.model.command

import java.util.ArrayDeque

class CommandHistoryStore {
    val history = ArrayDeque<LogisticsCommand>()
    val redoStack = ArrayDeque<LogisticsCommand>()

    fun clear() {
        history.clear()
        redoStack.clear()
    }
}
