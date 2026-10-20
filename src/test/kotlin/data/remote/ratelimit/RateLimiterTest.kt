package data.remote.ratelimit

import com.example.logiroute.data.remote.ratelimit.RateLimiter
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class RateLimiterTest {

    @Test
    fun `starts with full capacity and refuses when empty`() {
        val limiter = RateLimiter(
            maxBucketCapacity = 2,
            refillRatePerSecond = 5.0,
            nanoTime = { 0L }
        )

        assertTrue(limiter.tryAcquire())
        assertTrue(limiter.tryAcquire())
        assertFalse(limiter.tryAcquire())
    }

    @Test
    fun `refills tokens based on elapsed nanoseconds`() {
        var nowNanos = 0L
        val limiter = RateLimiter(
            maxBucketCapacity = 2,
            refillRatePerSecond = 5.0,
            nanoTime = { nowNanos }
        )

        assertTrue(limiter.tryAcquire())
        assertTrue(limiter.tryAcquire())
        assertFalse(limiter.tryAcquire())

        nowNanos += 200_000_000L

        assertTrue(limiter.tryAcquire())
        assertFalse(limiter.tryAcquire())
    }

    @Test
    fun `refill never exceeds bucket capacity`() {
        var nowNanos = 0L
        val limiter = RateLimiter(
            maxBucketCapacity = 2,
            refillRatePerSecond = 5.0,
            nanoTime = { nowNanos }
        )

        assertTrue(limiter.tryAcquire())
        nowNanos += 10_000_000_000L

        assertTrue(limiter.tryAcquire())
        assertTrue(limiter.tryAcquire())
        assertFalse(limiter.tryAcquire())
    }

    @Test
    fun `simultaneous requests cannot acquire more than available tokens`() {
        val limiter = RateLimiter(
            maxBucketCapacity = 10,
            refillRatePerSecond = 5.0,
            nanoTime = { 0L }
        )

        val executor = Executors.newFixedThreadPool(12)
        val startGate = CountDownLatch(1)

        try {
            val tasks = (1..40).map {
                executor.submit<Boolean> {
                    startGate.await()
                    limiter.tryAcquire()
                }
            }

            startGate.countDown()

            val acquiredCount = tasks.count { task ->
                task.get(2, TimeUnit.SECONDS)
            }

            assertEquals(10, acquiredCount)
        } finally {
            executor.shutdownNow()
        }
    }

    @Test
    fun `acquire waits until a token is refilled`() = runBlocking {
        val limiter = RateLimiter(
            maxBucketCapacity = 1,
            refillRatePerSecond = 10.0
        )

        assertTrue(limiter.tryAcquire())

        val startedAtNanos = System.nanoTime()
        limiter.acquire()
        val elapsedNanos = System.nanoTime() - startedAtNanos

        assertTrue(elapsedNanos >= 50_000_000L)
        assertFalse(limiter.tryAcquire())
    }
}