package com.yuliia.tmdb.domain.useCase

import androidx.paging.PagingData
import com.yuliia.tmdb.TestData
import com.yuliia.tmdb.domain.repository.Repository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test

class SearchMoviesUseCaseTest {

    private val repository: Repository = mockk()
    private val useCase = SearchMoviesUseCase(repository)

    @Test
    fun `invoke delegates to repository searchMovies with query`() = runTest {
        every { repository.searchMovies("batman") } returns flowOf(
            PagingData.from(listOf(TestData.movie(title = "Batman")))
        )

        useCase("batman").first()

        verify(exactly = 1) { repository.searchMovies("batman") }
    }
}
