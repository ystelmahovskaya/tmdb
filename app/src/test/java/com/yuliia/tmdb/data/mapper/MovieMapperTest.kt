package com.yuliia.tmdb.data.mapper

import com.yuliia.tmdb.TestData
import com.yuliia.tmdb.data.remote.dto.GenreDto
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieMapperTest {

    @Test
    fun `toDomain maps all fields correctly`() {
        val dto = TestData.movieDto(id = 42, title = "Inception")
        val movie = dto.toDomain()

        assertEquals(42, movie.id)
        assertEquals("Inception", movie.title)
        assertEquals("Test overview", movie.overview)
        assertEquals("2025-01-01", movie.releaseDate)
        assertEquals(7.5, movie.voteAverage, 0.01)
        assertEquals(100, movie.voteCount)
        assertEquals(50.0, movie.popularity, 0.01)
        assertEquals(listOf(28, 12), movie.genreIds)
    }

    @Test
    fun `toDomain builds full poster URL`() {
        val dto = TestData.movieDto()
        val movie = dto.toDomain()

        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", movie.posterPath)
    }

    @Test
    fun `toDomain builds full backdrop URL`() {
        val dto = TestData.movieDto()
        val movie = dto.toDomain()

        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", movie.backdropPath)
    }

    @Test
    fun `toDomain returns null poster when dto poster is null`() {
        val dto = TestData.movieDto().copy(posterPath = null)
        val movie = dto.toDomain()

        assertNull(movie.posterPath)
    }

    @Test
    fun `toDomain returns null backdrop when dto backdrop is null`() {
        val dto = TestData.movieDto().copy(backdropPath = null)
        val movie = dto.toDomain()

        assertNull(movie.backdropPath)
    }

    @Test
    fun `MovieListResponse toDomain maps all results`() {
        val response = TestData.movieListResponse(
            listOf(
                TestData.movieDto(id = 1, title = "Movie 1"),
                TestData.movieDto(id = 2, title = "Movie 2"),
                TestData.movieDto(id = 3, title = "Movie 3")
            )
        )
        val movies = response.toDomain()

        assertEquals(3, movies.size)
        assertEquals("Movie 1", movies[0].title)
        assertEquals("Movie 2", movies[1].title)
        assertEquals("Movie 3", movies[2].title)
    }

    @Test
    fun `MovieListResponse toDomain returns empty list for empty results`() {
        val response = TestData.movieListResponse(emptyList())
        val movies = response.toDomain()

        assertEquals(0, movies.size)
    }

    @Test
    fun `MovieDetailDto toDomain maps all fields correctly`() {
        val dto = TestData.movieDetailDto(id = 99, title = "Interstellar")
        val movie = dto.toDomain()

        assertEquals(99, movie.id)
        assertEquals("Interstellar", movie.title)
        assertEquals("Test overview", movie.overview)
        assertEquals("2025-01-01", movie.releaseDate)
        assertEquals(7.5, movie.voteAverage, 0.01)
        assertEquals(100, movie.voteCount)
        assertEquals(50.0, movie.popularity, 0.01)
    }

    @Test
    fun `MovieDetailDto toDomain maps genres to genreIds`() {
        val dto = TestData.movieDetailDto()
        val movie = dto.toDomain()

        assertEquals(listOf(28, 12), movie.genreIds)
    }

    @Test
    fun `MovieDetailDto toDomain handles empty genres`() {
        val dto = TestData.movieDetailDto().copy(genres = emptyList())
        val movie = dto.toDomain()

        assertEquals(emptyList<Int>(), movie.genreIds)
    }

    @Test
    fun `MovieDetailDto toDomain builds full image URLs`() {
        val dto = TestData.movieDetailDto()
        val movie = dto.toDomain()

        assertEquals("https://image.tmdb.org/t/p/w500/poster.jpg", movie.posterPath)
        assertEquals("https://image.tmdb.org/t/p/w780/backdrop.jpg", movie.backdropPath)
    }

    @Test
    fun `MovieDetailDto toDomain handles null images`() {
        val dto = TestData.movieDetailDto().copy(posterPath = null, backdropPath = null)
        val movie = dto.toDomain()

        assertNull(movie.posterPath)
        assertNull(movie.backdropPath)
    }
}
