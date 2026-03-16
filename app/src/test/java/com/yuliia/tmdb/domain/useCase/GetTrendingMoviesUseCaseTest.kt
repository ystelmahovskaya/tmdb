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

class GetTrendingMoviesUseCaseTest {

    private val repository: Repository = mockk()
    private val useCase = GetTrendingMoviesUseCase(repository)

    @Test
    fun `invoke delegates to repository getTrendingMovies`() = runTest {
        every { repository.getTrendingMovies() } returns flowOf(
            PagingData.from(listOf(TestData.movie()))
        )

        useCase().first()

        verify(exactly = 1) { repository.getTrendingMovies() }
    }
}
