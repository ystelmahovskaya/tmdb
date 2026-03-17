package com.yuliia.tmdb.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import android.util.Log
import com.yuliia.tmdb.data.local.MovieDao
import com.yuliia.tmdb.data.local.WatchlistDao
import com.yuliia.tmdb.data.local.WatchlistEntity
import kotlin.coroutines.cancellation.CancellationException
import com.yuliia.tmdb.data.local.toDomain
import com.yuliia.tmdb.data.local.toEntity
import com.yuliia.tmdb.data.mapper.toDomain
import com.yuliia.tmdb.data.paging.SearchMoviesPagingSource
import com.yuliia.tmdb.data.paging.TrendingRemoteMediator
import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.domain.repository.Repository
import com.yuliia.tmdb.util.TMDBResult
import com.yuliia.tmdb.util.safeFlow
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api: TMDBApi,
    private val dao: MovieDao,
    private val watchlistDao: WatchlistDao
) : Repository {

    // Room is the single source of truth; RemoteMediator handles network -> Room sync
    @OptIn(ExperimentalPagingApi::class)
    override fun getTrendingMovies(): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            remoteMediator = TrendingRemoteMediator(api, dao),
            pagingSourceFactory = { dao.getTrendingMoviesPaged() }
        ).flow.map { pagingData -> pagingData.map { it.toDomain() } }
    }

    // search is network-only, no caching since results change with every query
    override fun searchMovies(query: String): Flow<PagingData<Movie>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE, enablePlaceholders = false),
            pagingSourceFactory = { SearchMoviesPagingSource(api, query) }
        ).flow
    }

    override fun getMovieDetail(movieId: Int, forceRefresh: Boolean): Flow<TMDBResult<Movie>> {
        // on refresh/retry, skip cache and go straight to network with retry + backoff
        if (forceRefresh) {
            return safeFlow { fetchMovieDetail(movieId) }
                .onEach { if (it is TMDBResult.Success) dao.insertMovie(it.data.toEntity()) }
        }

        // cache-first: show cached data instantly, then update from network.
        // only emit error if there's no cached version to fall back on.
        return flow {
            emit(TMDBResult.Loading)

            val cached = dao.getMovieById(movieId)?.toDomain()
            if (cached != null) {
                emit(TMDBResult.Success(cached))
            }

            try {
                val remote = fetchMovieDetail(movieId)
                dao.insertMovie(remote.toEntity())
                emit(TMDBResult.Success(remote))
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (cached == null) {
                    emit(TMDBResult.Error(e))
                }
            }
        }
    }

    private suspend fun fetchMovieDetail(movieId: Int): Movie = coroutineScope {
        val detailDeferred = async { api.getMovieDetail(movieId).toDomain() }
        val trailerDeferred = async { getTrailerUrl(movieId) }
        detailDeferred.await().copy(trailerUrl = trailerDeferred.await())
    }

    private suspend fun getTrailerUrl(movieId: Int): String? {
        return try {
            val videos = api.getMovieVideos(movieId).results
            val trailer = videos.firstOrNull { it.site == "YouTube" && it.type == "Trailer" }
                ?: videos.firstOrNull { it.site == "YouTube" }
            trailer?.let { "https://www.youtube.com/watch?v=${it.key}" }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w("RepositoryImpl", "Failed to load trailer for movie $movieId", e)
            null
        }
    }

    // watchlist is local-only — no network layer, Room is the source of truth
    override fun getWatchlist(): Flow<List<Movie>> {
        return watchlistDao.getWatchlistMovies().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun isWatchlisted(movieId: Int): Flow<Boolean> {
        return watchlistDao.isWatchlisted(movieId)
    }

    override suspend fun toggleWatchlist(movieId: Int) {
        val exists = watchlistDao.isWatchlistedSync(movieId)
        if (exists) {
            watchlistDao.removeFromWatchlist(movieId)
        } else {
            watchlistDao.addToWatchlist(WatchlistEntity(movieId = movieId, addedAt = System.currentTimeMillis()))
        }
    }

    companion object {
        const val PAGE_SIZE = 20
    }
}
