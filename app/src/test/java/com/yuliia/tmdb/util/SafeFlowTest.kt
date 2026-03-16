package com.yuliia.tmdb.util

import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class SafeFlowTest {

    @Test
    fun `emits Loading then Success on successful call`() = runTest {
        val emissions = safeFlow { "hello" }.toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is TMDBResult.Loading)
        assertEquals("hello", (emissions[1] as TMDBResult.Success).data)
    }

    @Test
    fun `works with different types`() = runTest {
        val emissions = safeFlow { 42 }.toList()

        assertEquals(42, (emissions[1] as TMDBResult.Success).data)
    }

    @Test
    fun `emits Error on non-IOException without retrying`() = runTest {
        var attempt = 0
        val emissions = safeFlow<String>(retries = 3, initialDelayMs = 0) {
            attempt++
            throw IllegalStateException("parse error")
        }.toList()

        assertEquals(1, attempt)
        val last = emissions.last()
        assertTrue(last is TMDBResult.Error)
        assertEquals("parse error", (last as TMDBResult.Error).exception.message)
    }

    @Test
    fun `retries on IOException then succeeds`() = runTest {
        var attempt = 0
        val emissions = safeFlow(retries = 3, initialDelayMs = 0) {
            attempt++
            if (attempt < 3) throw IOException("network error")
            "recovered"
        }.toList()

        assertEquals(3, attempt)
        val last = emissions.last()
        assertTrue(last is TMDBResult.Success)
        assertEquals("recovered", (last as TMDBResult.Success).data)
    }

    @Test
    fun `emits Error after exhausting retries on IOException`() = runTest {
        var attempt = 0
        val emissions = safeFlow<String>(retries = 2, initialDelayMs = 0) {
            attempt++
            throw IOException("persistent failure")
        }.toList()

        assertEquals(3, attempt)
        val last = emissions.last()
        assertTrue(last is TMDBResult.Error)
        assertEquals("persistent failure", (last as TMDBResult.Error).exception.message)
    }

    @Test
    fun `does not retry on non-IOException`() = runTest {
        var attempt = 0
        val emissions = safeFlow<String>(retries = 3, initialDelayMs = 0) {
            attempt++
            throw RuntimeException("crash")
        }.toList()

        assertEquals(1, attempt)
        assertTrue(emissions.last() is TMDBResult.Error)
    }

    @Test
    fun `wraps non-Exception Throwable in Exception`() = runTest {
        val emissions = safeFlow<String>(retries = 1, initialDelayMs = 0) {
            throw OutOfMemoryError("oom")
        }.toList()

        val error = emissions.last() as TMDBResult.Error
        assertTrue(error.exception is Exception)
        assertEquals("oom", error.exception.message)
    }

    @Test
    fun `Loading is always the first emission`() = runTest {
        val success = safeFlow { "data" }.toList()
        assertTrue(success.first() is TMDBResult.Loading)

        val failure = safeFlow<String>(retries = 1, initialDelayMs = 0) {
            throw RuntimeException("fail")
        }.toList()
        assertTrue(failure.first() is TMDBResult.Loading)
    }
}
