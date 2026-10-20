package data.remote.retry

import com.example.logiroute.data.remote.retry.RetryCallbacks
import com.example.logiroute.data.remote.retry.RetryPolicy
import com.example.logiroute.data.remote.retry.retryWithBackoff
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RetryWithBackoffTest {

    @Test
    fun `cancellation exception is propagated without retry`() = runTest {
        var attempts = 0

        assertFailsWith<CancellationException> {
            retryWithBackoff<Unit>(
                policy = RetryPolicy(
                    maxRetries = 3,
                    initialDelayMs = 0
                )
            ) {
                attempts++
                throw CancellationException("Coroutine cancelled")
            }
        }

        assertEquals(1, attempts)
    }

    @Test
    fun `retries and succeeds when a later attempt works`() = runTest {
        var attempts = 0

        val result = retryWithBackoff(
            policy = RetryPolicy(
                maxRetries = 2,
                initialDelayMs = 0,
                isRetryable = { exception ->
                    exception is IllegalStateException
                }
            )
        ) {
            attempts++

            if (attempts < 3) {
                throw IllegalStateException("Temporary failure")
            }

            "success"
        }

        assertEquals("success", result.getOrThrow())
        assertEquals(3, attempts)
    }

    @Test
    fun `reports actual attempt count when retries are exhausted`() = runTest {
        var attempts = 0
        var reportedAttempts = 0

        val result = retryWithBackoff<String>(
            policy = RetryPolicy(
                maxRetries = 2,
                initialDelayMs = 0
            ),
            callbacks = RetryCallbacks(
                onGiveUp = { attempt, _, _ ->
                    reportedAttempts = attempt
                }
            )
        ) {
            attempts++
            throw IllegalStateException("Failure")
        }

        assertTrue(result.isFailure)
        assertEquals(3, attempts)
        assertEquals(3, reportedAttempts)
    }

    @Test
    fun `child failure cancels sibling coroutine`() = runTest {
        val siblingStarted = CompletableDeferred<Unit>()
        val siblingCancelled = CompletableDeferred<Unit>()

        assertFailsWith<IllegalStateException> {
            coroutineScope {
                launch {
                    try {
                        siblingStarted.complete(Unit)
                        awaitCancellation()
                    } finally {
                        siblingCancelled.complete(Unit)
                    }
                }

                siblingStarted.await()

                launch {
                    throw IllegalStateException("Child coroutine failed")
                }
            }
        }

        assertTrue(siblingCancelled.isCompleted)
    }
}