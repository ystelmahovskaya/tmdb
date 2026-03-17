package com.yuliia.tmdb.data.repository

import com.yuliia.tmdb.TestData
import com.yuliia.tmdb.data.local.MovieDao
import com.yuliia.tmdb.data.local.WatchlistDao
import com.yuliia.tmdb.data.local.toEntity
import com.yuliia.tmdb.util.TMDBResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class RepositoryImplTest {

    private lateinit var api: TMDBApi
    private lateinit var dao: MovieDao
    private lateinit var watchlistDao: WatchlistDao
    private lateinit var repository: RepositoryImpl

    @Before
    fun setUp() {
        api = mockk()
        dao = mockk(relaxUnitFun = true)
        watchlistDao = mockk(relaxUnitFun = true)
        coEvery { dao.getMovieById(any()) } returns null
        repository = RepositoryImpl(api, dao, watchlistDao)
    }

    @Test
    fun `getMovieDetail emits cached then network`() = runTest {
        val cachedEntity = TestData.movie(id = 42, title = "Cached Detail").toEntity()
        coEvery { dao.getMovieById(42) } returns cachedEntity
        val detailDto = TestData.movieDetailDto(id = 42, title = "Fresh Detail")
        coEvery { api.getMovieDetail(42) } returns detailDto
        coEvery { api.getMovieVideos(42) } returns TestData.emptyVideoResponse()

        val emissions = repository.getMovieDetail(42).toList()

        assertTrue(emissions[0] is TMDBResult.Loading)
        val cached = emissions[1] as TMDBResult.Success
        assertEquals("Cached Detail", cached.data.title)
        val fresh = emissions[2] as TMDBResult.Success
        assertEquals("Fresh Detail", fresh.data.title)
    }

    @Test
    fun `getMovieDetail emits Error when no cache and network fails`() = runTest {
        coEvery { api.getMovieDetail(99) } throws RuntimeException("Not found")
        coEvery { api.getMovieVideos(99) } throws RuntimeException("Not found")

        val emissions = repository.getMovieDetail(99).toList()

        assertTrue(emissions[0] is TMDBResult.Loading)
        val error = emissions[1] as TMDBResult.Error
        assertEquals("Not found", error.exception.message)
    }

    @Test
    fun `getMovieDetail caches network result`() = runTest {
        val detailDto = TestData.movieDetailDto(id = 42)
        coEvery { api.getMovieDetail(42) } returns detailDto
        coEvery { api.getMovieVideos(42) } returns TestData.emptyVideoResponse()

        repository.getMovieDetail(42).toList()

        coVerify { dao.insertMovie(any()) }
    }

    @Test
    fun `getMovieDetail silently succeeds when cached and network fails`() = runTest {
        val cachedEntity = TestData.movie(id = 42, title = "Cached").toEntity()
        coEvery { dao.getMovieById(42) } returns cachedEntity
        coEvery { api.getMovieDetail(42) } throws RuntimeException("Offline")
        coEvery { api.getMovieVideos(42) } throws RuntimeException("Offline")

        val emissions = repository.getMovieDetail(42).toList()

        assertTrue(emissions[0] is TMDBResult.Loading)
        val cached = emissions[1] as TMDBResult.Success
        assertEquals("Cached", cached.data.title)
        // no error emitted — cache covers the failure
        assertEquals(2, emissions.size)
    }

    @Test
    fun `getMovieDetail with forceRefresh skips cache`() = runTest {
        val cachedEntity = TestData.movie(id = 42, title = "Cached").toEntity()
        coEvery { dao.getMovieById(42) } returns cachedEntity
        val detailDto = TestData.movieDetailDto(id = 42, title = "Fresh")
        coEvery { api.getMovieDetail(42) } returns detailDto
        coEvery { api.getMovieVideos(42) } returns TestData.emptyVideoResponse()

        val emissions = repository.getMovieDetail(42, forceRefresh = true).toList()

        assertTrue(emissions[0] is TMDBResult.Loading)
        assertEquals(2, emissions.size)
        val success = emissions[1] as TMDBResult.Success
        assertEquals("Fresh", success.data.title)
    }

    @Test
    fun `getMovieDetail with forceRefresh emits Error on failure`() = runTest {
        val cachedEntity = TestData.movie(id = 42, title = "Cached").toEntity()
        coEvery { dao.getMovieById(42) } returns cachedEntity
        coEvery { api.getMovieDetail(42) } throws RuntimeException("Timeout")
        coEvery { api.getMovieVideos(42) } throws RuntimeException("Timeout")

        val emissions = repository.getMovieDetail(42, forceRefresh = true).toList()

        assertTrue(emissions[0] is TMDBResult.Loading)
        val error = emissions[1] as TMDBResult.Error
        assertEquals("Timeout", error.exception.message)
    }
}
