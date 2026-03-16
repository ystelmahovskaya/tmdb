package com.yuliia.tmdb.domain.useCase

import com.yuliia.tmdb.TestData
import com.yuliia.tmdb.domain.repository.Repository
import com.yuliia.tmdb.util.TMDBResult
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class GetMovieDetailUseCaseTest {

    private val repository: Repository = mockk()
    private val useCase = GetMovieDetailUseCase(repository)

    @Test
    fun `invoke delegates to repository getMovieDetail`() = runTest {
        val movie = TestData.movie(id = 42)
        every { repository.getMovieDetail(42) } returns flowOf(
            TMDBResult.Loading,
            TMDBResult.Success(movie)
        )

        val emissions = useCase(42).toList()

        assertEquals(2, emissions.size)
        assertTrue(emissions[0] is TMDBResult.Loading)
        val success = emissions[1] as TMDBResult.Success
        assertEquals(42, success.data.id)
        verify(exactly = 1) { repository.getMovieDetail(42) }
    }
}
