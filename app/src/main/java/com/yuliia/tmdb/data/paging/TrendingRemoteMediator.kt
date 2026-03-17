package com.yuliia.tmdb.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.yuliia.tmdb.data.local.MovieDao
import com.yuliia.tmdb.data.local.MovieEntity
import com.yuliia.tmdb.data.local.RemoteKeyEntity
import com.yuliia.tmdb.data.local.toEntity
import com.yuliia.tmdb.data.mapper.toDomain
import com.yuliia.tmdb.data.repository.TMDBApi
import com.yuliia.tmdb.domain.model.TimeWindow
import java.util.concurrent.TimeUnit

@OptIn(ExperimentalPagingApi::class)
class TrendingRemoteMediator(
    private val api: TMDBApi,
    private val dao: MovieDao,
    private val currentTimeMillis: () -> Long = System::currentTimeMillis
) : RemoteMediator<Int, MovieEntity>() {

    // skip network call if cache is less than 1 hour old
    override suspend fun initialize(): InitializeAction {
        val remoteKey = dao.getRemoteKey(REMOTE_KEY_ID)

        return if (remoteKey != null && currentTimeMillis() - remoteKey.lastUpdated < CACHE_TIMEOUT_MS) {
            InitializeAction.SKIP_INITIAL_REFRESH
        } else {
            InitializeAction.LAUNCH_INITIAL_REFRESH
        }
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, MovieEntity>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> 1
            LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
            LoadType.APPEND -> {
                val remoteKey = dao.getRemoteKey(REMOTE_KEY_ID)
                remoteKey?.nextPage ?: return MediatorResult.Success(endOfPaginationReached = true)
            }
        }

        return try {
            val response = api.getTrendingMovies(TimeWindow.WEEK.value, page)
            val movies = response.toDomain()
            val endReached = page >= response.totalPages

            val entities = movies.map { it.toEntity(isTrending = true, page = page) }
            val remoteKey = RemoteKeyEntity(
                id = REMOTE_KEY_ID,
                nextPage = if (endReached) null else page + 1,
                lastUpdated = currentTimeMillis()
            )

            if (loadType == LoadType.REFRESH) {
                // clear + insert in one transaction to avoid inconsistent state
                dao.refreshTrending(REMOTE_KEY_ID, entities, remoteKey)
            } else {
                dao.insertMovies(entities)
                dao.insertRemoteKey(remoteKey)
            }

            MediatorResult.Success(endOfPaginationReached = endReached)
        } catch (e: Exception) {
            MediatorResult.Error(e)
        }
    }

    companion object {
        const val REMOTE_KEY_ID = "trending"
        val CACHE_TIMEOUT_MS = TimeUnit.HOURS.toMillis(1)
    }
}
