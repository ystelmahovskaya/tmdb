package com.yuliia.tmdb.ui.screens.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.ui.theme.TmdbTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class HomeScreenTest {

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
    fun displaysMovieTitles() {
        val pagingFlow = MutableStateFlow(PagingData.from(movies))
        composeRule.setContent {
            TmdbTheme {
                HomeContent(
                    movies = pagingFlow.collectAsLazyPagingItems(),
                    searchQuery = "",
                    onSearchQueryChanged = {},
                    onMovieClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Inception").assertIsDisplayed()
        composeRule.onNodeWithText("Interstellar").assertIsDisplayed()
    }

    @Test
    fun displaysAppTitle() {
        val pagingFlow = MutableStateFlow(PagingData.from(movies))
        composeRule.setContent {
            TmdbTheme {
                HomeContent(
                    movies = pagingFlow.collectAsLazyPagingItems(),
                    searchQuery = "",
                    onSearchQueryChanged = {},
                    onMovieClick = {}
                )
            }
        }

        composeRule.onNodeWithText("TMDB").assertIsDisplayed()
    }

    @Test
    fun displaysEmptyState() {
        val pagingFlow = MutableStateFlow(PagingData.from(emptyList<Movie>()))
        composeRule.setContent {
            TmdbTheme {
                HomeContent(
                    movies = pagingFlow.collectAsLazyPagingItems(),
                    searchQuery = "",
                    onSearchQueryChanged = {},
                    onMovieClick = {}
                )
            }
        }

        composeRule.onNodeWithText("No movies found").assertIsDisplayed()
    }
}
