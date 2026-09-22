package com.example.logiroute.data.remote.retry

import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.io.IOException

fun isRetryable(error: Throwable): Boolean {
    return when (error) {
        is PostgrestRestException -> {
            error.response.status.value in listOf(503, 504)
        }

        is HttpRequestTimeoutException -> true

        is IOException -> true

        else -> false
    }
}