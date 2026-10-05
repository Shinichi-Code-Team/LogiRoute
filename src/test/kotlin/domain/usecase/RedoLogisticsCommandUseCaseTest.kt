package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.command.CommandHistoryStore
import com.example.logiroute.domain.model.command.LogisticsCommand
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RedoLogisticsCommandUseCaseTest {

    private lateinit var historyStore: CommandHistoryStore
    private lateinit var executeUseCase: ExecuteLogisticsCommandUseCase
    private lateinit var undoUseCase: UndoLogisticsCommandUseCase
    private lateinit var redoUseCase: RedoLogisticsCommandUseCase

    @BeforeEach
    fun setUp() {
        historyStore = CommandHistoryStore()
        executeUseCase = ExecuteLogisticsCommandUseCase(historyStore)
        undoUseCase = UndoLogisticsCommandUseCase(historyStore)
        redoUseCase = RedoLogisticsCommandUseCase(historyStore)
    }

    @Test
    fun `should return false when redo stack is empty`() {
        // Given - empty redo stack

        // When
        val result = redoUseCase()

        // Then
        assertFalse(result)
    }

    @Test
    fun `should return true when redoing an undone command`() {
        // Given
        val command = TestCommand(shouldSucceed = true)
        executeUseCase(command)
        undoUseCase()

        // When
        val result = redoUseCase()

        // Then
        assertTrue(result)
    }

    @Test
    fun `should re execute the undone command on redo`() {
        // Given
        val command = TestCommand(shouldSucceed = true)
        executeUseCase(command)
        undoUseCase()
        command.isExecuted = false

        // When
        redoUseCase()

        // Then
        assertTrue(command.isExecuted)
    }

    @Test
    fun `should increase history size after successful redo`() {
        // Given
        val command = TestCommand(shouldSucceed = true)
        executeUseCase(command)
        undoUseCase()

        // When
        redoUseCase()

        // Then
        assertEquals(1, executeUseCase.historySize)
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
