package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.command.LogisticsCommand
import com.example.logiroute.domain.model.command.TreeCommandHistoryStore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class UndoBranchingCommandUseCaseTest {

    private lateinit var treeStore: TreeCommandHistoryStore
    private lateinit var executeUseCase: ExecuteBranchingCommandUseCase
    private lateinit var undoUseCase: UndoBranchingCommandUseCase

    @BeforeEach
    fun setUp() {
        treeStore = TreeCommandHistoryStore()
        executeUseCase = ExecuteBranchingCommandUseCase(treeStore)
        undoUseCase = UndoBranchingCommandUseCase(treeStore)
    }

    @Test
    fun `should return true when undoing branching command in tree`() {
        // Given
        val command = TestCommand(shouldSucceed = true)
        executeUseCase(command)

        // When
        val result = undoUseCase()

        // Then
        assertTrue(result)
    }

    @Test
    fun `should return false when tree history has no parent to undo to`() {
        // Given - empty tree store

        // When
        val result = undoUseCase()

        // Then
        assertFalse(result)
    }

    @Test
    fun `should invoke undo on current node command`() {
        // Given
        val command = TestCommand(shouldSucceed = true)
        executeUseCase(command)

        // When
        undoUseCase()

        // Then
        assertTrue(command.isUndone)
    }

    @Test
    fun `should move currentNode to parent after undo`() {
        // Given
        val command1 = TestCommand(shouldSucceed = true)
        val command2 = TestCommand(shouldSucceed = true)
        executeUseCase(command1)
        executeUseCase(command2)

        // When
        undoUseCase()

        // Then
        assertEquals(command1, treeStore.currentNode?.command)
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
