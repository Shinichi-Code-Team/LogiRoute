package com.example.logiroute.data.remote.retry

import io.github.jan.supabase.postgrest.exception.PostgrestRestException
import io.ktor.client.plugins.HttpRequestTimeoutException
import java.io.IOException

fun isRetryable(error: Throwable): Boolean {
    return when (error) {
        is PostgrestRestException -> {
            val status = error.response.status.value
            status == 503 || status == 504
        }
        is HttpRequestTimeoutException, is IOException -> true
        else -> false
    }
}