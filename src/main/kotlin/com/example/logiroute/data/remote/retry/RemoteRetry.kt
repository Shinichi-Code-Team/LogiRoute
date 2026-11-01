package com.example.logiroute.data.remote.retry

import com.example.logiroute.data.remote.ratelimit.RateLimiter

suspend fun <T> retryRemote(
    rateLimiter: RateLimiter,
    block: suspend () -> T
): T {
    val result = retryWithBackoff(
        policy = RetryPolicy(isRetryable = ::isRetryable)
    ) {
        rateLimiter.acquire()
        block()
    }

    return result.getOrThrow()
}
