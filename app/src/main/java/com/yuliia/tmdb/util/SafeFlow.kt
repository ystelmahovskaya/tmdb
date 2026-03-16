package com.yuliia.tmdb.util

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.retry
import java.io.IOException

private const val DEFAULT_RETRIES = 3L
private const val DEFAULT_INITIAL_DELAY_MS = 1000L

/**
 * Wraps a suspend call in a Flow that emits Loading -> Success/Error,
 * with automatic retry + exponential backoff on network failures.
 */
fun <T> safeFlow(
    retries: Long = DEFAULT_RETRIES,
    initialDelayMs: Long = DEFAULT_INITIAL_DELAY_MS,
    call: suspend () -> T
): Flow<TMDBResult<T>> {
    var attempt = 0L
    return flow {
        emit(TMDBResult.Loading)
        val result = call()
        emit(TMDBResult.Success(result))
    }.retry(retries) { e ->
        // only retry network errors, fail fast on parsing/logic errors
        if (e is IOException) {
            delay(initialDelayMs * ++attempt)
            true
        } else {
            false
        }
    }.catch { e ->
        emit(TMDBResult.Error(if (e is Exception) e else Exception(e.message, e)))
    }
}
