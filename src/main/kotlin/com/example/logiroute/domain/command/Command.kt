package com.example.logiroute.domain.command

interface Command {
    fun execute()
    fun undo()
}