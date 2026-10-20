package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.CircuitBreakerState
import com.example.logiroute.domain.model.exceptions.LogisticsException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class CircuitBreakerUseCaseTest {

    private var simulatedTimeMs = 1000L
    private lateinit var circuitBreaker: CircuitBreakerUseCase

    @BeforeEach
    fun setUp() {
        simulatedTimeMs = 1000L
        circuitBreaker = CircuitBreakerUseCase(
            maxFailures = 5,
            coolDownMs = 5000L,
            currentTimeProvider = { simulatedTimeMs }
        )
    }

    @Test
    fun `should start in CLOSED state when initialized`() {
        // Given
        val expectedState = CircuitBreakerState.CLOSED

        // When
        val currentState = circuitBreaker.currentState

        // Then
        assertEquals(expectedState, currentState)
    }

    @Test
    fun `should start with zero failures when initialized`() {
        // Given
        val expectedFailures = 0

        // When
        val currentFailures = circuitBreaker.currentFailures

        // Then
        assertEquals(expectedFailures, currentFailures)
    }

    @Test
    fun `should return result when request succeeds in CLOSED state`() = runTest {
        // Given
        val expectedResult = "Success Data"

        // When
        val result = circuitBreaker { expectedResult }

        // Then
        assertEquals(expectedResult, result)
    }

    @Test
    fun `should reset failure counter when request succeeds in CLOSED state`() = runTest {
        // Given
        repeat(3) {
            runCatching { circuitBreaker { throw RuntimeException("Network Error") } }
        }

        // When
        circuitBreaker { "Success Data" }

        // Then
        assertEquals(0, circuitBreaker.currentFailures)
    }

    @Test
    fun `should remain in CLOSED state when failures are below threshold`() = runTest {
        // Given & When
        repeat(4) {
            runCatching { circuitBreaker { throw RuntimeException("Network Error") } }
        }

        // Then
        assertEquals(CircuitBreakerState.CLOSED, circuitBreaker.currentState)
    }

    @Test
    fun `should transition to OPEN state when failures reach threshold`() = runTest {
        // Given & When
        repeat(5) {
            runCatching { circuitBreaker { throw RuntimeException("Network Error") } }
        }

        // Then
        assertEquals(CircuitBreakerState.OPEN, circuitBreaker.currentState)
    }

    @Test
    fun `should count 5 failures when threshold is reached`() = runTest {
        // Given & When
        repeat(5) {
            runCatching { circuitBreaker { throw RuntimeException("Network Error") } }
        }

        // Then
        assertEquals(5, circuitBreaker.currentFailures)
    }

    @Test
    fun `should throw CallNotPermittedException when executing request in OPEN state`() = runTest {
        // Given
        repeat(5) {
            runCatching { circuitBreaker { throw RuntimeException("Network Error") } }
        }

        // When & Then
        assertThrows(LogisticsException.CallNotPermittedException::class.java) {
            runBlocking {
                circuitBreaker { "Data" }
            }
        }
    }

    @Test
    fun `should not execute action when state is OPEN`() = runTest {
        // Given
        repeat(5) {
            runCatching { circuitBreaker { throw RuntimeException("Network Error") } }
        }
        var actionExecuted = false

        // When
        runCatching {
            runBlocking {
                circuitBreaker {
                    actionExecuted = true
                    "Data"
                }
            }
        }

        // Then
        assertFalse(actionExecuted)
    }

    @Test
    fun `should remain in OPEN state before cool down period elapses`() = runTest {
        // Given
        repeat(5) {
            runCatching { circuitBreaker { throw RuntimeException("Network Error") } }
        }

        // When
        simulatedTimeMs += 3000L // 3s < 5s coolDownMs

        // Then
        assertEquals(CircuitBreakerState.OPEN, circuitBreaker.currentState)
    }

    @Test
    fun `should transition to CLOSED state when probe succeeds after cool down`() = runTest {
        // Given
        repeat(5) {
            runCatching { circuitBreaker { throw RuntimeException("Network Error") } }
        }
        simulatedTimeMs += 5000L

        // When
        circuitBreaker { "Probe Data" }

        // Then
        assertEquals(CircuitBreakerState.CLOSED, circuitBreaker.currentState)
    }

    @Test
    fun `should reset failures to zero when probe succeeds after cool down`() = runTest {
        // Given
        repeat(5) {
            runCatching { circuitBreaker { throw RuntimeException("Network Error") } }
        }
        simulatedTimeMs += 5000L

        // When
        circuitBreaker { "Probe Data" }

        // Then
        assertEquals(0, circuitBreaker.currentFailures)
    }

    @Test
    fun `should return probe result when probe succeeds after cool down`() = runTest {
        // Given
        repeat(5) {
            runCatching { circuitBreaker { throw RuntimeException("Network Error") } }
        }
        simulatedTimeMs += 5000L

        // When
        val result = circuitBreaker { "Probe Data" }

        // Then
        assertEquals("Probe Data", result)
    }

    @Test
    fun `should return to OPEN state if probe request fails in HALF_OPEN state`() = runTest {
        // Given
        repeat(5) {
            runCatching { circuitBreaker { throw RuntimeException("Network Error") } }
        }
        simulatedTimeMs += 5000L

        // When
        runCatching {
            runBlocking {
                circuitBreaker { throw RuntimeException("Probe Failed") }
            }
        }

        // Then
        assertEquals(CircuitBreakerState.OPEN, circuitBreaker.currentState)
    }

    @Test
    fun `should not increment failures when CallNotPermittedException is thrown`() = runTest {
        // Given
        repeat(5) {
            runCatching { circuitBreaker { throw RuntimeException("Network Error") } }
        }
        val failuresCountBefore = circuitBreaker.currentFailures

        // When
        runCatching {
            runBlocking {
                circuitBreaker { "Data" }
            }
        }

        // Then
        assertEquals(failuresCountBefore, circuitBreaker.currentFailures)
    }
}
