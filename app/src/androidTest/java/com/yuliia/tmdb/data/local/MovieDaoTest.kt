package com.yuliia.tmdb.data.local

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MovieDaoTest {

    private lateinit var database: TMDBDatabase
    private lateinit var dao: MovieDao
    private lateinit var watchlistDao: WatchlistDao

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TMDBDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.movieDao()
        watchlistDao = database.watchlistDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun entity(
        id: Int = 1,
        title: String = "Test",
        isTrending: Boolean = false
    ) = MovieEntity(
        id = id,
        title = title,
        overview = "Overview",
        posterPath = null,
        backdropPath = null,
        releaseDate = "2025-01-01",
        voteAverage = 7.0,
        voteCount = 100,
        popularity = 50.0,
        genreIds = "28",
        isTrending = isTrending
    )

    @Test
    fun insertAndGetMovieById() = runTest {
        dao.insertMovie(entity(id = 42, title = "Inception"))

        val movie = dao.getMovieById(42)

        assertNotNull(movie)
        assertEquals("Inception", movie!!.title)
    }

    @Test
    fun getMovieByIdReturnsNullWhenNotFound() = runTest {
        val movie = dao.getMovieById(999)
        assertNull(movie)
    }

    @Test
    fun insertMovieReplacesOnConflict() = runTest {
        dao.insertMovie(entity(id = 1, title = "Old Title"))
        dao.insertMovie(entity(id = 1, title = "New Title"))

        val movie = dao.getMovieById(1)

        assertEquals("New Title", movie!!.title)
    }

    @Test
    fun clearTrendingRemovesNonWatchlistedMovies() = runTest {
        dao.insertMovies(listOf(entity(id = 1, isTrending = true)))

        dao.clearTrending()

        assertNull(dao.getMovieById(1))
    }

    @Test
    fun clearTrendingDoesNotRemoveWatchlistedMovies() = runTest {
        dao.insertMovies(listOf(entity(id = 1, title = "Watchlisted", isTrending = true)))
        watchlistDao.addToWatchlist(WatchlistEntity(movieId = 1, addedAt = 0))

        dao.clearTrending()

        val movie = dao.getMovieById(1)
        assertNotNull(movie)
        assertEquals("Watchlisted", movie!!.title)
    }

    @Test
    fun clearTrendingRemovesOnlyNonWatchlistedFromMix() = runTest {
        dao.insertMovies(
            listOf(
                entity(id = 1, title = "Saved", isTrending = true),
                entity(id = 2, title = "Not Saved", isTrending = true),
                entity(id = 3, title = "Not Trending", isTrending = false)
            )
        )
        watchlistDao.addToWatchlist(WatchlistEntity(movieId = 1, addedAt = 0))

        dao.clearTrending()

        assertNotNull(dao.getMovieById(1))  // watchlisted, kept
        assertNull(dao.getMovieById(2))     // trending only, deleted
        assertNotNull(dao.getMovieById(3))  // not trending, untouched
    }
}
