package com.example.logiroute.domain.command

interface CommandInvoker {

    fun executeCommand(command: Command)

    fun undo(): Boolean

    fun redo(): Boolean

    fun historySize(): Int
}