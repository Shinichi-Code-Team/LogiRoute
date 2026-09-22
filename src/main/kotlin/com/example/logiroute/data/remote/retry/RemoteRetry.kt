package com.example.logiroute.data.remote.retry

suspend fun <T> retryRemote(
    block: suspend () -> T
): T {

    val result = retryWithBackoff(
        isRetryable = ::isRetryable,

        onRetry = { attempt, delayMs, error ->
            println(
                "[Retry] attempt=$attempt " +
                        "delay=${delayMs}ms " +
                        "error=${error::class.simpleName}"
            )
        },

        onGiveUp = { attempt, error, reason ->
            println(
                "[Retry] stopped attempt=$attempt " +
                        "reason=$reason " +
                        "error=${error::class.simpleName}"
            )
        },

        block = block
    )

    result.onSuccess {
        println("[Retry] success")
    }

    return result.getOrThrow()
}