package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.command.LogisticsCommand
import com.example.logiroute.domain.model.command.TreeCommandHistoryStore
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class RedoBranchingCommandUseCaseTest {

    private lateinit var treeStore: TreeCommandHistoryStore
    private lateinit var executeUseCase: ExecuteBranchingCommandUseCase
    private lateinit var undoUseCase: UndoBranchingCommandUseCase
    private lateinit var redoUseCase: RedoBranchingCommandUseCase

    @BeforeEach
    fun setUp() {
        treeStore = TreeCommandHistoryStore()
        executeUseCase = ExecuteBranchingCommandUseCase(treeStore)
        undoUseCase = UndoBranchingCommandUseCase(treeStore)
        redoUseCase = RedoBranchingCommandUseCase(treeStore)
    }

    @Test
    fun `should return false when tree has no child node to redo`() {
        // Given - empty tree store

        // When
        val result = redoUseCase()

        // Then
        assertFalse(result)
    }

    @Test
    fun `should return true when redoing undone command in tree`() {
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
    fun `should invoke execute again on child node command during redo`() {
        // Given
        val command = TestCommand(shouldSucceed = true)
        executeUseCase(command)
        undoUseCase()
        command.isExecuted = false // reset flag to verify re-execution

        // When
        redoUseCase()

        // Then
        assertTrue(command.isExecuted)
    }

    @Test
    fun `should advance currentNode to child node after redo`() {
        // Given
        val command = TestCommand(shouldSucceed = true)
        executeUseCase(command)
        undoUseCase()

        // When
        redoUseCase()

        // Then
        assertEquals(command, treeStore.currentNode?.command)
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
