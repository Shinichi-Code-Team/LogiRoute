package com.example.logiroute.data.remote.ratelimit

import kotlinx.coroutines.delay
import java.util.concurrent.atomic.AtomicReference
import kotlin.math.ceil

class RateLimiter(
    private val maxBucketCapacity: Int,
    private val refillRatePerSecond: Double,
    private val nanoTime: () -> Long = System::nanoTime
) {
    private data class BucketState(val tokens: Double, val updatedAtNanos: Long)

    private val capacity = maxBucketCapacity.toDouble()

    private val state: AtomicReference<BucketState>

    init {
        require(maxBucketCapacity > 0) {
            "maxBucketCapacity must be greater than zero."
        }
        require(refillRatePerSecond.isFinite() && refillRatePerSecond > 0.0) {
            "refillRatePerSecond must be finite and greater than zero."
        }

        state = AtomicReference(
            BucketState(
                tokens = capacity, updatedAtNanos = nanoTime()
            )
        )
    }

    fun tryAcquire(): Boolean {
        while (true) {
            val currentState = state.get()
            val now = nanoTime()
            val refilledState = refill(currentState, now)
            val hasToken = refilledState.tokens >= 1.0

            val updatedState = refilledState.copy(
                tokens = if (hasToken) refilledState.tokens - 1.0
                else refilledState.tokens
            )

            if (state.compareAndSet(currentState, updatedState)) {
                return hasToken
            }
        }
    }

    suspend fun acquire() {
        while (!tryAcquire()) {
            delay(nanosUntilNextToken().toCeilingMillis())
        }
    }

    private fun nanosUntilNextToken(): Long {
        val currentState = state.get()
        val refilledState = refill(currentState, nanoTime())
        val missingTokens = (1.0 - refilledState.tokens).coerceAtLeast(0.0)

        val waitNanos = ceil(
            missingTokens * NANOS_PER_SECOND / refillRatePerSecond
        ).toLong()

        return waitNanos.coerceAtLeast(1L)
    }

    private fun refill(
        currentState: BucketState, nowNanos: Long
    ): BucketState {
        val elapsedNanos = (nowNanos - currentState.updatedAtNanos).coerceAtLeast(0L)

        val addedTokens = elapsedNanos.toDouble() * refillRatePerSecond / NANOS_PER_SECOND

        return BucketState(
            tokens = (currentState.tokens + addedTokens).coerceAtMost(capacity), updatedAtNanos = nowNanos
        )
    }

    private fun Long.toCeilingMillis(): Long {
        val wholeMillis = this / NANOS_PER_MILLISECOND
        val hasRemainingNanos = this % NANOS_PER_MILLISECOND != 0L

        return (wholeMillis + if (hasRemainingNanos) 1 else 0).coerceAtLeast(1L)
    }

    private companion object {
        const val NANOS_PER_SECOND = 1_000_000_000.0
        const val NANOS_PER_MILLISECOND = 1_000_000L
    }
}