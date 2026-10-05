package com.example.logiroute.domain.usecase

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExecuteLogisticsCommandUseCaseTest {

    private lateinit var useCase: ExecuteLogisticsCommandUseCase

    @BeforeEach
    fun setUp() {
        useCase = ExecuteLogisticsCommandUseCase()
    }

    @Test
    fun `should return true when command executes successfully`() {
        // Given
        val dummyCommand = TestCommand(shouldSucceed = true)

        // When
        val result = useCase(dummyCommand)

        // Then
        assertTrue(result)
    }

    @Test
    fun `should return false when command execution fails`() {
        // Given
        val failingCommand = TestCommand(shouldSucceed = false)

        // When
        val result = useCase(failingCommand)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should increase history size after executing successful command`() {
        // Given
        val dummyCommand = TestCommand(shouldSucceed = true)

        // When
        useCase(dummyCommand)

        // Then
        assertEquals(1, useCase.getHistorySize())
    }

    @Test
    fun `should not increase history size when command execution fails`() {
        // Given
        val failingCommand = TestCommand(shouldSucceed = false)

        // When
        useCase(failingCommand)

        // Then
        assertEquals(0, useCase.getHistorySize())
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
