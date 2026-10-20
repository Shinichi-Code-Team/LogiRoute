package com.example.logiroute.data.remote.retry

import kotlinx.coroutines.delay
import kotlin.coroutines.cancellation.CancellationException

data class RetryPolicy(
    val maxRetries: Int = 3,
    val initialDelayMs: Long = 1_000,
    val factor: Double = 2.0,
    val isRetryable: (Throwable) -> Boolean = { true }
) {
    init {
        require(maxRetries >= 0) { "maxRetries cannot be negative" }
        require(initialDelayMs >= 0) { "initialDelayMs cannot be negative" }
        require(factor >= 1.0) { "factor must be at least 1.0" }
    }
}

data class RetryCallbacks(
    val onRetry: (Int, Long, Throwable) -> Unit = { _, _, _ -> },
    val onGiveUp: (Int, Throwable, String) -> Unit = { _, _, _ -> }
)

suspend fun <T> retryWithBackoff(
    policy: RetryPolicy = RetryPolicy(),
    callbacks: RetryCallbacks = RetryCallbacks(),
    block: suspend () -> T
): Result<T> {
    var currentDelay = policy.initialDelayMs
    var attemptNumber = 1

    while (true) {
        try {
            return Result.success(block())
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: Exception) {
            if (!policy.isRetryable(exception)) {
                callbacks.onGiveUp(
                    attemptNumber,
                    exception,
                    "Non-retryable exception encountered"
                )
                return Result.failure(exception)
            }

            if (attemptNumber > policy.maxRetries) {
                callbacks.onGiveUp(
                    attemptNumber,
                    exception,
                    "Max retries limit (${policy.maxRetries}) reached"
                )
                return Result.failure(exception)
            }

            callbacks.onRetry(attemptNumber, currentDelay, exception)
            delay(currentDelay)

            currentDelay = (currentDelay * policy.factor).toLong()
            attemptNumber++
        }
    }
}