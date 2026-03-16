package com.yuliia.tmdb.util


sealed class TMDBResult<out T> {
    data class Success<T>(val data: T) : TMDBResult<T>()
    data class Error(val exception: Throwable, val message: String? = null) : TMDBResult<Nothing>()
    object Loading : TMDBResult<Nothing>()
}