package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.command.CommandHistoryStore
import com.example.logiroute.domain.model.command.LogisticsCommand
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UndoLogisticsCommandUseCaseTest {

    private lateinit var historyStore: CommandHistoryStore
    private lateinit var executeUseCase: ExecuteLogisticsCommandUseCase
    private lateinit var undoUseCase: UndoLogisticsCommandUseCase

    @BeforeEach
    fun setUp() {
        historyStore = CommandHistoryStore()
        executeUseCase = ExecuteLogisticsCommandUseCase(historyStore)
        undoUseCase = UndoLogisticsCommandUseCase(historyStore)
    }

    @Test
    fun `should return true when undoing an executed command`() {
        // Given
        val command = TestCommand(shouldSucceed = true)
        executeUseCase(command)

        // When
        val result = undoUseCase()

        // Then
        assertTrue(result)
    }

    @Test
    fun `should return false when history is empty`() {
        // Given - empty history

        // When
        val result = undoUseCase()

        // Then
        assertFalse(result)
    }

    @Test
    fun `should invoke undo on the last executed command`() {
        // Given
        val command = TestCommand(shouldSucceed = true)
        executeUseCase(command)

        // When
        undoUseCase()

        // Then
        assertTrue(command.isUndone)
    }

    @Test
    fun `should decrease history size after successful undo`() {
        // Given
        val command = TestCommand(shouldSucceed = true)
        executeUseCase(command)

        // When
        undoUseCase()

        // Then
        assertEquals(0, executeUseCase.historySize)
    }

    private class TestCommand(private val shouldSucceed: Boolean) : LogisticsCommand {
        var isExecuted = false
        var isUndone = false

        override fun execute(): Boolean {
            isExecuted = shouldSucceed
            return shouldSucceed
        }

        override fun undo(): Boolean {
            isUndone = true
            return true
        }
    }
}
