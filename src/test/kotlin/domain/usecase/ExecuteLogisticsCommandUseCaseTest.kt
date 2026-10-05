package domain.usecase

import com.example.logiroute.domain.model.command.LogisticsCommand
import com.example.logiroute.domain.usecase.ExecuteLogisticsCommandUseCase
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ExecuteLogisticsCommandUseCaseTest {

    private val useCase = ExecuteLogisticsCommandUseCase()

    @Test
    fun `should execute command successfully and record it in history`() {
        // Given
        val testCommand = TestCommand(shouldSucceed = true)

        // When
        val result = useCase(testCommand)

        // Then
        assertTrue(result)
    }

    @Test
    fun `should increase history size after successful command execution`() {
        // Given
        val testCommand = TestCommand(shouldSucceed = true)

        // When
        useCase(testCommand)

        // Then
        assertEquals(1, useCase.historySize())
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
    fun `should not increase history size when command execution fails`() {
        // Given
        val failingCommand = TestCommand(shouldSucceed = false)

        // When
        useCase(failingCommand)

        // Then
        assertEquals(0, useCase.historySize())
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
