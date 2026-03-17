package com.yuliia.tmdb.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import com.yuliia.tmdb.TestData
import com.yuliia.tmdb.domain.repository.Repository
import com.yuliia.tmdb.domain.useCase.GetMovieDetailUseCase
import com.yuliia.tmdb.domain.useCase.IsWatchlistedUseCase
import com.yuliia.tmdb.domain.useCase.ToggleWatchlistUseCase
import com.yuliia.tmdb.ui.navigation.Screen
import com.yuliia.tmdb.util.TMDBResult
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DetailViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: Repository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
        every { repository.isWatchlisted(any()) } returns flowOf(false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun createViewModel(movieId: Int): DetailViewModel {
        val savedStateHandle = SavedStateHandle(mapOf(Screen.ARG_MOVIE_ID to movieId))
        return DetailViewModel(
            savedStateHandle,
            GetMovieDetailUseCase(repository),
            IsWatchlistedUseCase(repository),
            ToggleWatchlistUseCase(repository)
        )
    }

    private fun createViewModelWithoutId(): DetailViewModel {
        val savedStateHandle = SavedStateHandle()
        return DetailViewModel(
            savedStateHandle,
            GetMovieDetailUseCase(repository),
            IsWatchlistedUseCase(repository),
            ToggleWatchlistUseCase(repository)
        )
    }

    @Test
    fun `initial state is Loading`() = runTest(testDispatcher) {
        every { repository.getMovieDetail(1, false) } answers { flowOf(TMDBResult.Loading) }

        val viewModel = createViewModel(1)

        assertTrue(viewModel.movie.value is TMDBResult.Loading)
    }

    @Test
    fun `loads movie detail on init with cache`() = runTest(testDispatcher) {
        val movie = TestData.movie(id = 42, title = "Inception")
        every { repository.getMovieDetail(42, false) } answers {
            flowOf(TMDBResult.Success(movie))
        }

        val viewModel = createViewModel(42)
        advanceUntilIdle()

        val state = viewModel.movie.value as TMDBResult.Success
        assertEquals("Inception", state.data.title)
    }

    @Test
    fun `emits Error when API fails`() = runTest(testDispatcher) {
        every { repository.getMovieDetail(1, false) } answers {
            flowOf(TMDBResult.Error(RuntimeException("Not found")))
        }

        val viewModel = createViewModel(1)
        advanceUntilIdle()

        val state = viewModel.movie.value as TMDBResult.Error
        assertEquals("Not found", state.exception.message)
    }

    @Test
    fun `emits Error when movieId is missing`() = runTest(testDispatcher) {
        val viewModel = createViewModelWithoutId()

        val state = viewModel.movie.value as TMDBResult.Error
        assertEquals("Movie not found", state.exception.message)
    }

    @Test
    fun `retry calls with forceRefresh true`() = runTest(testDispatcher) {
        every { repository.getMovieDetail(1, false) } answers {
            flowOf(TMDBResult.Error(RuntimeException("Network error")))
        }
        every { repository.getMovieDetail(1, true) } answers {
            flowOf(TMDBResult.Success(TestData.movie()))
        }

        val viewModel = createViewModel(1)
        advanceUntilIdle()
        assertTrue(viewModel.movie.value is TMDBResult.Error)

        viewModel.retry()
        advanceUntilIdle()

        val state = viewModel.movie.value as TMDBResult.Success
        assertEquals("Test Movie", state.data.title)
        verify { repository.getMovieDetail(1, true) }
    }

    @Test
    fun `refresh calls with forceRefresh true`() = runTest(testDispatcher) {
        every { repository.getMovieDetail(1, false) } answers {
            flowOf(TMDBResult.Success(TestData.movie(title = "Cached")))
        }
        every { repository.getMovieDetail(1, true) } answers {
            flowOf(TMDBResult.Success(TestData.movie(title = "Fresh")))
        }

        val viewModel = createViewModel(1)
        advanceUntilIdle()
        assertEquals("Cached", (viewModel.movie.value as TMDBResult.Success).data.title)

        viewModel.refresh()
        advanceUntilIdle()

        assertEquals("Fresh", (viewModel.movie.value as TMDBResult.Success).data.title)
        verify { repository.getMovieDetail(1, true) }
    }

    @Test
    fun `refresh sets isRefreshing true then false after result`() = runTest(testDispatcher) {
        every { repository.getMovieDetail(1, false) } answers {
            flowOf(TMDBResult.Success(TestData.movie()))
        }
        every { repository.getMovieDetail(1, true) } answers {
            flowOf(TMDBResult.Success(TestData.movie(title = "Fresh")))
        }

        val viewModel = createViewModel(1)
        advanceUntilIdle()
        assertFalse(viewModel.isRefreshing.value)

        viewModel.refresh()
        assertTrue(viewModel.isRefreshing.value)

        advanceUntilIdle()
        assertFalse(viewModel.isRefreshing.value)
    }

    @Test
    fun `refresh sets isRefreshing false on error`() = runTest(testDispatcher) {
        every { repository.getMovieDetail(1, false) } answers {
            flowOf(TMDBResult.Success(TestData.movie()))
        }
        every { repository.getMovieDetail(1, true) } answers {
            flowOf(TMDBResult.Error(RuntimeException("Offline")))
        }

        val viewModel = createViewModel(1)
        advanceUntilIdle()

        viewModel.refresh()
        assertTrue(viewModel.isRefreshing.value)

        advanceUntilIdle()
        assertFalse(viewModel.isRefreshing.value)
    }

    @Test
    fun `retry does not set isRefreshing`() = runTest(testDispatcher) {
        every { repository.getMovieDetail(1, false) } answers {
            flowOf(TMDBResult.Error(RuntimeException("fail")))
        }
        every { repository.getMovieDetail(1, true) } answers {
            flowOf(TMDBResult.Success(TestData.movie()))
        }

        val viewModel = createViewModel(1)
        advanceUntilIdle()

        viewModel.retry()
        assertFalse(viewModel.isRefreshing.value)
    }

    @Test
    fun `isWatchlisted reflects repository state`() = runTest(testDispatcher) {
        every { repository.getMovieDetail(1, false) } answers {
            flowOf(TMDBResult.Success(TestData.movie()))
        }
        every { repository.isWatchlisted(1) } returns flowOf(true)

        val viewModel = createViewModel(1)
        // need an active collector for WhileSubscribed to start emitting
        val job = launch { viewModel.isWatchlisted.collect {} }
        advanceUntilIdle()

        assertTrue(viewModel.isWatchlisted.value)
        job.cancel()
    }

    @Test
    fun `isWatchlisted defaults to false`() = runTest(testDispatcher) {
        every { repository.getMovieDetail(1, false) } answers {
            flowOf(TMDBResult.Success(TestData.movie()))
        }

        val viewModel = createViewModel(1)

        assertFalse(viewModel.isWatchlisted.value)
    }

    @Test
    fun `toggleWatchlist calls repository`() = runTest(testDispatcher) {
        every { repository.getMovieDetail(1, false) } answers {
            flowOf(TMDBResult.Success(TestData.movie()))
        }
        coEvery { repository.toggleWatchlist(1) } returns Unit

        val viewModel = createViewModel(1)
        advanceUntilIdle()

        viewModel.toggleWatchlist()
        advanceUntilIdle()

        coVerify { repository.toggleWatchlist(1) }
    }
}
