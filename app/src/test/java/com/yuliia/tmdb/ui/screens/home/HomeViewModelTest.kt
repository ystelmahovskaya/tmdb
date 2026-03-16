package com.yuliia.tmdb.ui.screens.home

import androidx.paging.PagingData
import com.yuliia.tmdb.TestData
import com.yuliia.tmdb.domain.repository.Repository
import com.yuliia.tmdb.domain.useCase.GetTrendingMoviesUseCase
import com.yuliia.tmdb.domain.useCase.SearchMoviesUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private lateinit var repository: Repository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    private fun stubTrending() {
        every { repository.getTrendingMovies() } answers {
            flowOf(PagingData.from(listOf(TestData.movie())))
        }
    }

    private fun stubSearch(query: String) {
        every { repository.searchMovies(query) } answers {
            flowOf(PagingData.from(listOf(TestData.movie(title = "Search Result"))))
        }
    }

    private fun createViewModel(): HomeViewModel {
        return HomeViewModel(
            getTrendingMovies = GetTrendingMoviesUseCase(repository),
            searchMovies = SearchMoviesUseCase(repository)
        )
    }

    @Test
    fun `initial search query is empty`() = runTest(testDispatcher) {
        stubTrending()
        val viewModel = createViewModel()
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `search query updates state`() = runTest(testDispatcher) {
        stubTrending()
        val viewModel = createViewModel()
        viewModel.onSearchQueryChanged("hello")
        assertEquals("hello", viewModel.searchQuery.value)
    }

    @Test
    fun `loads trending on init when collected`() = runTest(testDispatcher) {
        stubTrending()
        val viewModel = createViewModel()
        advanceTimeBy(400)
        viewModel.movies.first()
        verify { repository.getTrendingMovies() }
    }

    @Test
    fun `search triggers when collected`() = runTest(testDispatcher) {
        stubTrending()
        stubSearch("batman")

        val viewModel = createViewModel()
        advanceTimeBy(400)
        viewModel.movies.first()

        viewModel.onSearchQueryChanged("batman")
        advanceTimeBy(400)
        viewModel.movies.first()

        verify { repository.searchMovies("batman") }
    }

    @Test
    fun `clearing search query switches back to trending`() = runTest(testDispatcher) {
        stubTrending()
        stubSearch("batman")

        val viewModel = createViewModel()
        advanceTimeBy(400)
        viewModel.movies.first()

        viewModel.onSearchQueryChanged("batman")
        advanceTimeBy(400)
        viewModel.movies.first()
        verify { repository.searchMovies("batman") }

        viewModel.onSearchQueryChanged("")
        advanceTimeBy(400)
        viewModel.movies.first()

        // trending should be called at least twice: once on init, once after clearing search
        verify(atLeast = 2) { repository.getTrendingMovies() }
    }

    @Test
    fun `short query does not trigger search`() = runTest(testDispatcher) {
        stubTrending()
        val viewModel = createViewModel()
        advanceTimeBy(400)
        viewModel.movies.first()

        viewModel.onSearchQueryChanged("ab")
        advanceTimeBy(400)

        verify(exactly = 0) { repository.searchMovies(any()) }
    }
}
