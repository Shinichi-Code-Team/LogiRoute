package com.example.logiroute.data.remote.retry

import kotlinx.coroutines.delay

suspend fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    factor: Double = 2.0,
    isRetryable: (Throwable) -> Boolean = { true },
    onRetry: (attempt: Int, delayMs: Long, error: Throwable) -> Unit = { _, _, _ -> },
    onGiveUp: (attempt: Int, error: Throwable, reason: String) -> Unit = { _, _, _ -> },
    block: suspend () -> T
): Result<T> {
    require(maxRetries >= 0) { "maxRetries cannot be negative" }
    require(initialDelayMs >= 0) { "initialDelayMs cannot be negative" }
    require(factor >= 1.0) { "factor must be at least 1.0" }

    var currentDelay = initialDelayMs
    var attemptNumber = 1

    while (true) {
        try {
            return Result.success(block())
        } catch (e: Throwable) {
            if (!isRetryable(e)) {
                onGiveUp(attemptNumber, e, "Non-retryable exception encountered")
                return Result.failure(e)
            }

            if (attemptNumber > maxRetries) {
                onGiveUp(attemptNumber - 1, e, "Max retries limit ($maxRetries) reached")
                return Result.failure(e)
            }

            onRetry(attemptNumber, currentDelay, e)

            delay(currentDelay)
            currentDelay = (currentDelay * factor).toLong()
            attemptNumber++
        }
    }
}