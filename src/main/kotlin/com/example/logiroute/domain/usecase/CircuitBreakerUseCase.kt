package com.example.logiroute.domain.usecase

import com.example.logiroute.domain.model.CircuitBreakerState
import com.example.logiroute.domain.model.exceptions.LogisticsException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference

class CircuitBreakerUseCase(
    private val maxFailures: Int = 5,
    private val coolDownMs: Long = 5000L,
    private val currentTimeProvider: () -> Long = { System.currentTimeMillis() }
) {
    private val currentStateRef = AtomicReference(CircuitBreakerState.CLOSED)
    private val failureCounter = AtomicInteger(0)
    private val lastStateChangeTimeMs = AtomicLong(currentTimeProvider())
    private val isProbeAllowed = AtomicBoolean(false)

    val currentState: CircuitBreakerState
        get() = checkAndUpdateState()

    val currentFailures: Int
        get() = failureCounter.get()

    private fun checkAndUpdateState(): CircuitBreakerState {
        val state = currentStateRef.get()
        if (state == CircuitBreakerState.OPEN) {
            val elapsedTime = currentTimeProvider() - lastStateChangeTimeMs.get()
            if (elapsedTime >= coolDownMs) {
                if (currentStateRef.compareAndSet(CircuitBreakerState.OPEN, CircuitBreakerState.HALF_OPEN)) {
                    lastStateChangeTimeMs.set(currentTimeProvider())
                    isProbeAllowed.set(true)
                    return CircuitBreakerState.HALF_OPEN
                }
            }
        }
        return currentStateRef.get()
    }

    suspend operator fun <T> invoke(action: suspend () -> T): T {
        when (checkAndUpdateState()) {
            CircuitBreakerState.OPEN -> {
                throw LogisticsException.CallNotPermittedException("Circuit Breaker is OPEN. Fast failing request.")
            }
            CircuitBreakerState.HALF_OPEN -> {
                if (!isProbeAllowed.compareAndSet(true, false)) {
                    throw LogisticsException.CallNotPermittedException("Circuit Breaker is HALF_OPEN. Probe request already in progress.")
                }
            }
            CircuitBreakerState.CLOSED -> { /* Normal execution allowed */ }
        }

        return try {
            val result = action()
            handleSuccess()
            result
        } catch (e: Throwable) {
            if (e !is LogisticsException.CallNotPermittedException) {
                handleFailure()
            }
            throw e
        }
    }

    private fun handleSuccess() {
        val state = currentStateRef.get()
        if (state == CircuitBreakerState.HALF_OPEN) {
            if (currentStateRef.compareAndSet(CircuitBreakerState.HALF_OPEN, CircuitBreakerState.CLOSED)) {
                failureCounter.set(0)
                lastStateChangeTimeMs.set(currentTimeProvider())
            }
        } else if (state == CircuitBreakerState.CLOSED) {
            failureCounter.set(0)
        }
    }

    private fun handleFailure() {
        while (true) {
            val state = currentStateRef.get()
            when (state) {
                CircuitBreakerState.CLOSED -> {
                    val failures = failureCounter.incrementAndGet()
                    if (failures >= maxFailures) {
                        if (currentStateRef.compareAndSet(CircuitBreakerState.CLOSED, CircuitBreakerState.OPEN)) {
                            lastStateChangeTimeMs.set(currentTimeProvider())
                            break
                        }
                    } else break
                }
                CircuitBreakerState.HALF_OPEN -> {
                    if (currentStateRef.compareAndSet(CircuitBreakerState.HALF_OPEN, CircuitBreakerState.OPEN)) {
                        lastStateChangeTimeMs.set(currentTimeProvider())
                        break
                    }
                }
                CircuitBreakerState.OPEN -> break
            }
        }
    }
}
