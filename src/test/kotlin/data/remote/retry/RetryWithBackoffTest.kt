package data.remote.retry

import com.example.logiroute.data.remote.retry.retryWithBackoff
import kotlinx.coroutines.*
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class RetryWithBackoffTest {
    @Test
    fun `cancellation exception is propagated without retry`() = runTest {
        // Given
        var attempts = 0

        // When & Then
        assertFailsWith<CancellationException> {
            retryWithBackoff(
                maxRetries = 3,
                initialDelayMs = 0
            ) {
                attempts++

                throw CancellationException(
                    "Coroutine cancelled"
                )
            }
        }

        assertEquals(
            expected = 1,
            actual = attempts
        )
    }


    @Test
    fun `child failure cancels sibling coroutine`() = runTest {
        val siblingStarted = CompletableDeferred<Unit>()
        val siblingCancelled = CompletableDeferred<Unit>()
        assertFailsWith<IllegalStateException> {
            coroutineScope {
                async {
                    try {
                        siblingStarted.complete(Unit)
                        awaitCancellation()
                    } finally {
                        siblingCancelled.complete(Unit)
                    }
                }

                siblingStarted.await()

                async {
                    throw IllegalStateException(
                        "Child coroutine failed"
                    )
                }.await()
            }
        }

        assertTrue(
            siblingCancelled.isCompleted
        )
    }
}