package com.example.logiroute.domain.usecase.command

interface Command {
    fun execute()
    fun undo()
}