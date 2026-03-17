package com.yuliia.tmdb.ui.screens.watchlist

import com.yuliia.tmdb.TestData
import com.yuliia.tmdb.domain.repository.Repository
import com.yuliia.tmdb.domain.useCase.GetWatchlistUseCase
import com.yuliia.tmdb.domain.useCase.ToggleWatchlistUseCase
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class WatchlistViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: Repository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk(relaxUnitFun = true)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(): WatchlistViewModel {
        return WatchlistViewModel(
            GetWatchlistUseCase(repository),
            ToggleWatchlistUseCase(repository)
        )
    }

    @Test
    fun `initial state is empty list`() = runTest(testDispatcher) {
        every { repository.getWatchlist() } returns flowOf(emptyList())

        val viewModel = createViewModel()
        val job = launch { viewModel.movies.collect {} }
        advanceUntilIdle()

        assertEquals(emptyList<Any>(), viewModel.movies.value)
        job.cancel()
    }

    @Test
    fun `emits watchlist movies from repository`() = runTest(testDispatcher) {
        val movies = listOf(
            TestData.movie(id = 1, title = "Inception"),
            TestData.movie(id = 2, title = "Interstellar")
        )
        every { repository.getWatchlist() } returns flowOf(movies)

        val viewModel = createViewModel()
        val job = launch { viewModel.movies.collect {} }
        advanceUntilIdle()

        assertEquals(2, viewModel.movies.value.size)
        assertEquals("Inception", viewModel.movies.value[0].title)
        assertEquals("Interstellar", viewModel.movies.value[1].title)
        job.cancel()
    }

    @Test
    fun `removeFromWatchlist calls toggle`() = runTest(testDispatcher) {
        every { repository.getWatchlist() } returns flowOf(emptyList())

        val viewModel = createViewModel()
        viewModel.removeFromWatchlist(42)
        advanceUntilIdle()

        coVerify { repository.toggleWatchlist(42) }
    }
}
