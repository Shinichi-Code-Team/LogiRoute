package com.example.logiroute.data.remote.retry

import io.github.jan.supabase.postgrest.exception.PostgrestRestException

suspend fun <T> retryRemote(
    operationName: String = "RemoteCall",
    block: suspend () -> T
): T {
    val result = retryWithBackoff(
        isRetryable = ::isRetryable,
        onRetry = { attempt, delayMs, error ->
            println(
                "[RetryTelemetry::$operationName] Attempt #$attempt failed. " +
                        "Cause: ${describeError(error)} -> Retrying in ${delayMs}ms..."
            )
        },
        onGiveUp = { attempt, error, reason ->
            println(
                "[RetryTelemetry::$operationName] Stopped after $attempt attempt(s). " +
                        "Reason: \"$reason\" | Cause: ${describeError(error)}"
            )
        },
        block = block
    )

    result.onSuccess {
        println("[RetryTelemetry::$operationName] Operation succeeded successfully.")
    }

    return result.getOrThrow()
}

private fun describeError(error: Throwable): String {
    return if (error is PostgrestRestException) {
        "${error::class.simpleName}(status=${error.response.status.value})"
    } else {
        error::class.simpleName ?: "UnknownError"
    }
}