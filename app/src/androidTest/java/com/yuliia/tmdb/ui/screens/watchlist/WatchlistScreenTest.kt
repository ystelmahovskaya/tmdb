package com.yuliia.tmdb.ui.screens.watchlist

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.ui.theme.TmdbTheme
import org.junit.Rule
import org.junit.Test

class WatchlistScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val movies = listOf(
        Movie(
            id = 1,
            title = "Inception",
            overview = "A mind-bending thriller",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2010-07-16",
            voteAverage = 8.4,
            voteCount = 30000,
            popularity = 100.0,
            genreIds = listOf(28)
        ),
        Movie(
            id = 2,
            title = "Interstellar",
            overview = "A space epic",
            posterPath = null,
            backdropPath = null,
            releaseDate = "2014-11-07",
            voteAverage = 8.6,
            voteCount = 28000,
            popularity = 90.0,
            genreIds = listOf(12)
        )
    )

    @Test
    fun displaysWatchlistTitle() {
        composeRule.setContent {
            TmdbTheme {
                WatchlistContent(
                    movies = movies,
                    onMovieClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Watchlist").assertIsDisplayed()
    }

    @Test
    fun displaysMovieTitles() {
        composeRule.setContent {
            TmdbTheme {
                WatchlistContent(
                    movies = movies,
                    onMovieClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Inception").assertIsDisplayed()
        composeRule.onNodeWithText("Interstellar").assertIsDisplayed()
    }

    @Test
    fun displaysMovieOverviews() {
        composeRule.setContent {
            TmdbTheme {
                WatchlistContent(
                    movies = movies,
                    onMovieClick = {}
                )
            }
        }

        composeRule.onNodeWithText("A mind-bending thriller").assertIsDisplayed()
    }

    @Test
    fun displaysEmptyState() {
        composeRule.setContent {
            TmdbTheme {
                WatchlistContent(
                    movies = emptyList(),
                    onMovieClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Your watchlist is empty").assertIsDisplayed()
    }
}
