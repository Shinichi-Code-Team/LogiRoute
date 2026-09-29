package com.example.logiroute.domain.command

import com.example.logiroute.domain.command.Command

interface CommandInvoker {

    fun executeCommand(command: Command)

    fun undo(): Boolean

    fun redo(): Boolean

    fun historySize(): Int
}