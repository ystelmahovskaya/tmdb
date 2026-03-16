package com.yuliia.tmdb.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.yuliia.tmdb.TestData
import com.yuliia.tmdb.data.local.MovieDao
import com.yuliia.tmdb.data.local.MovieEntity
import com.yuliia.tmdb.data.local.RemoteKeyEntity
import com.yuliia.tmdb.data.repository.TMDBApi
import com.yuliia.tmdb.domain.model.TimeWindow
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalPagingApi::class)
class TrendingRemoteMediatorTest {

    private lateinit var api: TMDBApi
    private lateinit var dao: MovieDao
    private var fakeTime = 1_000_000L

    @Before
    fun setUp() {
        api = mockk()
        dao = mockk(relaxUnitFun = true)
        coEvery { dao.getRemoteKey(any()) } returns null
    }

    private fun createMediator() = TrendingRemoteMediator(api, dao) { fakeTime }

    private fun emptyPagingState() = PagingState<Int, MovieEntity>(
        pages = emptyList(),
        anchorPosition = null,
        config = PagingConfig(pageSize = 20),
        leadingPlaceholderCount = 0
    )

    @Test
    fun `refresh loads page 1 and clears old data`() = runTest {
        val response = TestData.movieListResponse(
            listOf(TestData.movieDto(id = 1), TestData.movieDto(id = 2))
        ).copy(page = 1, totalPages = 5)
        coEvery { api.getTrendingMovies(TimeWindow.WEEK.value, 1) } returns response

        val result = createMediator().load(LoadType.REFRESH, emptyPagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
        coVerify { dao.refreshTrending(TrendingRemoteMediator.REMOTE_KEY_ID, any(), any()) }
    }

    @Test
    fun `append loads next page`() = runTest {
        coEvery { dao.getRemoteKey(TrendingRemoteMediator.REMOTE_KEY_ID) } returns RemoteKeyEntity(
            id = TrendingRemoteMediator.REMOTE_KEY_ID,
            nextPage = 2,
            lastUpdated = fakeTime
        )
        val response = TestData.movieListResponse().copy(page = 2, totalPages = 5)
        coEvery { api.getTrendingMovies(TimeWindow.WEEK.value, 2) } returns response

        val result = createMediator().load(LoadType.APPEND, emptyPagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertFalse((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `append returns end reached when no next page`() = runTest {
        coEvery { dao.getRemoteKey(TrendingRemoteMediator.REMOTE_KEY_ID) } returns RemoteKeyEntity(
            id = TrendingRemoteMediator.REMOTE_KEY_ID,
            nextPage = null,
            lastUpdated = fakeTime
        )

        val result = createMediator().load(LoadType.APPEND, emptyPagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `prepend always returns end reached`() = runTest {
        val result = createMediator().load(LoadType.PREPEND, emptyPagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `returns error on network failure`() = runTest {
        coEvery { api.getTrendingMovies(TimeWindow.WEEK.value, 1) } throws RuntimeException("Offline")

        val result = createMediator().load(LoadType.REFRESH, emptyPagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Error)
    }

    @Test
    fun `single page returns end reached true`() = runTest {
        val response = TestData.movieListResponse().copy(page = 1, totalPages = 1)
        coEvery { api.getTrendingMovies(TimeWindow.WEEK.value, 1) } returns response

        val result = createMediator().load(LoadType.REFRESH, emptyPagingState())

        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `append does not clear existing data`() = runTest {
        coEvery { dao.getRemoteKey(TrendingRemoteMediator.REMOTE_KEY_ID) } returns RemoteKeyEntity(
            id = TrendingRemoteMediator.REMOTE_KEY_ID,
            nextPage = 3,
            lastUpdated = fakeTime
        )
        val response = TestData.movieListResponse().copy(page = 3, totalPages = 5)
        coEvery { api.getTrendingMovies(TimeWindow.WEEK.value, 3) } returns response

        createMediator().load(LoadType.APPEND, emptyPagingState())

        coVerify(exactly = 0) { dao.refreshTrending(any(), any(), any()) }
        coVerify { dao.insertMovies(any()) }
        coVerify { dao.insertRemoteKey(any()) }
    }

    @Test
    fun `append returns end reached on last page`() = runTest {
        coEvery { dao.getRemoteKey(TrendingRemoteMediator.REMOTE_KEY_ID) } returns RemoteKeyEntity(
            id = TrendingRemoteMediator.REMOTE_KEY_ID,
            nextPage = 5,
            lastUpdated = fakeTime
        )
        val response = TestData.movieListResponse().copy(page = 5, totalPages = 5)
        coEvery { api.getTrendingMovies(TimeWindow.WEEK.value, 5) } returns response

        val result = createMediator().load(LoadType.APPEND, emptyPagingState())

        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }

    @Test
    fun `initialize skips refresh when cache is fresh`() = runTest {
        coEvery { dao.getRemoteKey(TrendingRemoteMediator.REMOTE_KEY_ID) } returns RemoteKeyEntity(
            id = TrendingRemoteMediator.REMOTE_KEY_ID,
            nextPage = 2,
            lastUpdated = fakeTime - 30 * 60 * 1000 // 30 minutes ago
        )

        val action = createMediator().initialize()

        assertTrue(action == RemoteMediator.InitializeAction.SKIP_INITIAL_REFRESH)
    }

    @Test
    fun `initialize launches refresh when cache is stale`() = runTest {
        coEvery { dao.getRemoteKey(TrendingRemoteMediator.REMOTE_KEY_ID) } returns RemoteKeyEntity(
            id = TrendingRemoteMediator.REMOTE_KEY_ID,
            nextPage = 2,
            lastUpdated = fakeTime - 2 * 60 * 60 * 1000 // 2 hours ago
        )

        val action = createMediator().initialize()

        assertTrue(action == RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH)
    }

    @Test
    fun `initialize launches refresh at exact cache timeout boundary`() = runTest {
        coEvery { dao.getRemoteKey(TrendingRemoteMediator.REMOTE_KEY_ID) } returns RemoteKeyEntity(
            id = TrendingRemoteMediator.REMOTE_KEY_ID,
            nextPage = 2,
            lastUpdated = fakeTime - TrendingRemoteMediator.CACHE_TIMEOUT_MS // exactly 1 hour ago
        )

        val action = createMediator().initialize()

        assertTrue(action == RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH)
    }

    @Test
    fun `initialize launches refresh when no remote key`() = runTest {
        coEvery { dao.getRemoteKey(TrendingRemoteMediator.REMOTE_KEY_ID) } returns null

        val action = createMediator().initialize()

        assertTrue(action == RemoteMediator.InitializeAction.LAUNCH_INITIAL_REFRESH)
    }

    @Test
    fun `append returns end reached when no remote key`() = runTest {
        coEvery { dao.getRemoteKey(TrendingRemoteMediator.REMOTE_KEY_ID) } returns null

        val result = createMediator().load(LoadType.APPEND, emptyPagingState())

        assertTrue(result is RemoteMediator.MediatorResult.Success)
        assertTrue((result as RemoteMediator.MediatorResult.Success).endOfPaginationReached)
    }
}
