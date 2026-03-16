package com.yuliia.tmdb.data.local

import com.yuliia.tmdb.TestData
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MovieEntityMapperTest {

    @Test
    fun `toEntity maps all fields`() {
        val movie = TestData.movie(id = 1, title = "Inception")
        val entity = movie.toEntity()

        assertEquals(1, entity.id)
        assertEquals("Inception", entity.title)
        assertEquals("Test overview", entity.overview)
        assertEquals("2025-01-01", entity.releaseDate)
        assertEquals(7.5, entity.voteAverage, 0.01)
        assertEquals(100, entity.voteCount)
        assertEquals(50.0, entity.popularity, 0.01)
        assertEquals("28,12", entity.genreIds)
    }

    @Test
    fun `toDomain maps all fields`() {
        val entity = MovieEntity(
            id = 42,
            title = "Interstellar",
            overview = "Space epic",
            posterPath = "https://image.tmdb.org/t/p/w500/poster.jpg",
            backdropPath = "https://image.tmdb.org/t/p/w780/backdrop.jpg",
            releaseDate = "2014-11-07",
            voteAverage = 8.6,
            voteCount = 28000,
            popularity = 90.0,
            genreIds = "12,878"
        )
        val movie = entity.toDomain()

        assertEquals(42, movie.id)
        assertEquals("Interstellar", movie.title)
        assertEquals("Space epic", movie.overview)
        assertEquals("2014-11-07", movie.releaseDate)
        assertEquals(8.6, movie.voteAverage, 0.01)
        assertEquals(28000, movie.voteCount)
        assertEquals(90.0, movie.popularity, 0.01)
        assertEquals(listOf(12, 878), movie.genreIds)
    }

    @Test
    fun `toDomain handles null image paths`() {
        val entity = TestData.movie().toEntity().copy(posterPath = null, backdropPath = null)
        val movie = entity.toDomain()

        assertNull(movie.posterPath)
        assertNull(movie.backdropPath)
    }

    @Test
    fun `toDomain handles empty genreIds`() {
        val entity = TestData.movie().toEntity().copy(genreIds = "")
        val movie = entity.toDomain()

        assertEquals(emptyList<Int>(), movie.genreIds)
    }

    @Test
    fun `roundtrip preserves data`() {
        val original = TestData.movie(id = 99, title = "Roundtrip")
        val roundtripped = original.toEntity().toDomain()

        assertEquals(original.id, roundtripped.id)
        assertEquals(original.title, roundtripped.title)
        assertEquals(original.overview, roundtripped.overview)
        assertEquals(original.posterPath, roundtripped.posterPath)
        assertEquals(original.backdropPath, roundtripped.backdropPath)
        assertEquals(original.releaseDate, roundtripped.releaseDate)
        assertEquals(original.voteAverage, roundtripped.voteAverage, 0.01)
        assertEquals(original.voteCount, roundtripped.voteCount)
        assertEquals(original.popularity, roundtripped.popularity, 0.01)
        assertEquals(original.genreIds, roundtripped.genreIds)
    }
}
