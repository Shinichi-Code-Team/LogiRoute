package com.example.logiroute.data.remote.retry

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.delay

suspend fun <T> retryWithBackoff(
    maxRetries: Int = 3,
    initialDelayMs: Long = 1000,
    factor: Double = 2.0,
    isRetryable: (Throwable) -> Boolean,
    onRetry: (attempt: Int, delayMs: Long, error: Throwable) -> Unit = { _, _, _ -> },
    onGiveUp: (attempt: Int, error: Throwable, reason: String) -> Unit = { _, _, _ -> },
    block: suspend () -> T
): Result<T> {

    require(maxRetries >= 0) {
        "maxRetries must be >= 0"
    }

    require(initialDelayMs >= 0) {
        "initialDelayMs must be >= 0"
    }

    require(factor >= 1.0) {
        "factor must be >= 1.0"
    }

    return attempt(
        attemptNumber = 1,
        maxRetries = maxRetries,
        delayMs = initialDelayMs,
        factor = factor,
        isRetryable = isRetryable,
        onRetry = onRetry,
        onGiveUp = onGiveUp,
        block = block
    )
}

private suspend fun <T> attempt(
    attemptNumber: Int,
    maxRetries: Int,
    delayMs: Long,
    factor: Double,
    isRetryable: (Throwable) -> Boolean,
    onRetry: (Int, Long, Throwable) -> Unit,
    onGiveUp: (Int, Throwable, String) -> Unit,
    block: suspend () -> T
): Result<T> {

    val result = runCatching {
        block()
    }

    val error = result.exceptionOrNull()

    if (error is CancellationException) {
        throw error
    }

    return when {
        error == null -> result

        !isRetryable(error) -> {
            onGiveUp(
                attemptNumber,
                error,
                "non-retryable error"
            )
            result
        }

        attemptNumber > maxRetries -> {
            onGiveUp(
                attemptNumber,
                error,
                "max retries ($maxRetries) exhausted"
            )
            result
        }

        else -> {
            onRetry(
                attemptNumber,
                delayMs,
                error
            )

            delay(delayMs)

            attempt(
                attemptNumber = attemptNumber + 1,
                maxRetries = maxRetries,
                delayMs = (delayMs * factor).toLong(),
                factor = factor,
                isRetryable = isRetryable,
                onRetry = onRetry,
                onGiveUp = onGiveUp,
                block = block
            )
        }
    }
}