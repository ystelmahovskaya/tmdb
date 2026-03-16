package com.yuliia.tmdb.ui.screens.detail

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.ui.theme.TmdbTheme
import com.yuliia.tmdb.util.TMDBResult
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class DetailScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val testMovie = Movie(
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
    )

    private val movieWithTrailer = testMovie.copy(
        trailerUrl = "https://www.youtube.com/watch?v=abc123"
    )

    @Test
    fun displaysMovieDetailsOnSuccess() {
        composeRule.setContent {
            TmdbTheme {
                DetailContent(
                    movieState = TMDBResult.Success(testMovie),
                    onBackClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Inception").assertIsDisplayed()
        composeRule.onNodeWithText("Overview").assertIsDisplayed()
        composeRule.onNodeWithText("A mind-bending thriller").assertIsDisplayed()
        composeRule.onNodeWithText("2010-07-16").assertIsDisplayed()
    }

    @Test
    fun displaysBackButton() {
        composeRule.setContent {
            TmdbTheme {
                DetailContent(
                    movieState = TMDBResult.Success(testMovie),
                    onBackClick = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun displaysLoadingState() {
        composeRule.setContent {
            TmdbTheme {
                DetailContent(
                    movieState = TMDBResult.Loading,
                    onBackClick = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Loading").assertIsDisplayed()
    }

    @Test
    fun displaysGenericErrorMessage() {
        composeRule.setContent {
            TmdbTheme {
                DetailContent(
                    movieState = TMDBResult.Error(RuntimeException("some internal error")),
                    onBackClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    @Test
    fun displaysRetryButtonOnError() {
        var retryCalled = false
        composeRule.setContent {
            TmdbTheme {
                DetailContent(
                    movieState = TMDBResult.Error(RuntimeException("Failed")),
                    onBackClick = {},
                    onRetry = { retryCalled = true }
                )
            }
        }

        composeRule.onNodeWithText("Try again").assertIsDisplayed()
        composeRule.onNodeWithText("Try again").performClick()
        assertTrue(retryCalled)
    }

    @Test
    fun displaysTrailerButtonWhenUrlAvailable() {
        composeRule.setContent {
            TmdbTheme {
                DetailContent(
                    movieState = TMDBResult.Success(movieWithTrailer),
                    onBackClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Watch Trailer").assertIsDisplayed()
    }

    @Test
    fun hidesTrailerButtonWhenNoUrl() {
        composeRule.setContent {
            TmdbTheme {
                DetailContent(
                    movieState = TMDBResult.Success(testMovie),
                    onBackClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Watch Trailer").assertDoesNotExist()
    }

    @Test
    fun trailerButtonPassesCorrectUrl() {
        var clickedUrl = ""
        composeRule.setContent {
            TmdbTheme {
                DetailContent(
                    movieState = TMDBResult.Success(movieWithTrailer),
                    onBackClick = {},
                    onTrailerClick = { clickedUrl = it }
                )
            }
        }

        composeRule.onNodeWithText("Watch Trailer").performClick()
        assertEquals("https://www.youtube.com/watch?v=abc123", clickedUrl)
    }
}
