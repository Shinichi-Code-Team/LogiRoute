package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.command.LogisticsCommand
import com.example.logiroute.domain.model.command.TreeCommandHistoryStore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ExecuteBranchingCommandUseCaseTest {

    private lateinit var historyStore: TreeCommandHistoryStore
    private lateinit var useCase: ExecuteBranchingCommandUseCase

    @BeforeEach
    fun setUp() {
        historyStore = TreeCommandHistoryStore()
        useCase = ExecuteBranchingCommandUseCase(historyStore)
    }

    @Test
    fun `should return true when branching command executes successfully`() {
        // Given
        val command = TestCommand(shouldSucceed = true)

        // When
        val result = useCase(command)

        // Then
        assertTrue(result)
    }

    @Test
    fun `should return false when branching command execution fails`() {
        // Given
        val command = TestCommand(shouldSucceed = false)

        // When
        val result = useCase(command)

        // Then
        assertFalse(result)
    }

    @Test
    fun `should advance current node in tree when command succeeds`() {
        // Given
        val command = TestCommand(shouldSucceed = true)

        // When
        useCase(command)

        // Then
        assertNotNull(historyStore.currentNode)
    }

    @Test
    fun `should not advance current node in tree when command fails`() {
        // Given
        val command = TestCommand(shouldSucceed = false)

        // When
        useCase(command)

        // Then
        assertNull(historyStore.currentNode)
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