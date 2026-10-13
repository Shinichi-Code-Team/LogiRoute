package com.example.logiroute.domain.model.command

interface LogisticsCommand {
    fun execute(): Boolean
    fun undo(): Boolean
}
