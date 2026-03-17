package com.yuliia.tmdb.domain.repository

import androidx.paging.PagingData
import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.util.TMDBResult
import kotlinx.coroutines.flow.Flow

interface Repository {
    fun getTrendingMovies(): Flow<PagingData<Movie>>
    fun searchMovies(query: String): Flow<PagingData<Movie>>
    fun getMovieDetail(movieId: Int, forceRefresh: Boolean = false): Flow<TMDBResult<Movie>>
    fun getWatchlist(): Flow<List<Movie>>
    fun isWatchlisted(movieId: Int): Flow<Boolean>
    suspend fun toggleWatchlist(movieId: Int)
}
