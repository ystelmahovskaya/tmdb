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

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(context, TMDBDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        dao = database.movieDao()
    }

    @After
    fun tearDown() {
        database.close()
    }

    private fun entity(
        id: Int = 1,
        title: String = "Test"
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
        genreIds = "28"
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
}
