package com.example.logiroute.presentation.cli

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal suspend fun readCliInput(prompt: String): String? =
    withContext(Dispatchers.IO) {
        print("$prompt ")
        readlnOrNull()?.trim()
    }

internal suspend fun readFiniteCliDouble(prompt: String): Double? =
    readCliInput(prompt)
        ?.toDoubleOrNull()
        ?.takeIf { value -> value.isFinite() }

internal suspend fun safelyRunCliCommand(action: suspend () -> Unit) {
    try {
        action()
    } catch (cancellation: CancellationException) {
        throw cancellation
    } catch (error: Exception) {
        println("Command failed: ${error.message}")
    }
}
