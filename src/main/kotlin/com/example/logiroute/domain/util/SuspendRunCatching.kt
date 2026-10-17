
package com.example.logiroute.domain.util

import kotlinx.coroutines.CancellationException

suspend fun <T> suspendRunCatching(
    block: suspend () -> T
): Result<T> {
    return try {
        Result.success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: Exception) {
        Result.failure(e)
    }
}
