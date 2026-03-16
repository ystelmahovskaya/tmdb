package com.yuliia.tmdb.ui.navigation

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.paging.PagingData
import androidx.paging.compose.collectAsLazyPagingItems
import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.ui.screens.detail.DetailContent
import com.yuliia.tmdb.ui.screens.home.HomeContent
import com.yuliia.tmdb.ui.theme.TmdbTheme
import com.yuliia.tmdb.util.TMDBResult
import kotlinx.coroutines.flow.MutableStateFlow
import org.junit.Rule
import org.junit.Test

class NavGraphTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val testMovies = listOf(
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
        )
    )

    private val pagingFlow = MutableStateFlow(PagingData.from(testMovies))

    @Test
    fun startsOnHomeScreen() {
        composeRule.setContent {
            TmdbTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) {
                        HomeContent(
                            movies = pagingFlow.collectAsLazyPagingItems(),
                            searchQuery = "",
                            onSearchQueryChanged = {},
                            onMovieClick = {}
                        )
                    }
                }
            }
        }

        composeRule.onNodeWithText("TMDB").assertIsDisplayed()
        composeRule.onNodeWithText("Inception").assertIsDisplayed()
    }

    @Test
    fun navigatesToDetailScreen() {
        composeRule.setContent {
            TmdbTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) {
                        HomeContent(
                            movies = pagingFlow.collectAsLazyPagingItems(),
                            searchQuery = "",
                            onSearchQueryChanged = {},
                            onMovieClick = { movie ->
                                navController.navigate(Screen.Detail.createRoute(movie.id))
                            }
                        )
                    }
                    composable(
                        route = Screen.Detail.route,
                        arguments = listOf(navArgument(Screen.ARG_MOVIE_ID) { type = NavType.IntType })
                    ) {
                        DetailContent(
                            movieState = TMDBResult.Success(testMovies[0]),
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }

        composeRule.onNodeWithText("Inception").performClick()
        composeRule.onNodeWithText("Overview").assertIsDisplayed()
        composeRule.onNodeWithText("A mind-bending thriller").assertIsDisplayed()
    }

    @Test
    fun detailBackButtonNavigatesHome() {
        composeRule.setContent {
            TmdbTheme {
                val navController = rememberNavController()
                NavHost(navController = navController, startDestination = Screen.Home.route) {
                    composable(Screen.Home.route) {
                        HomeContent(
                            movies = pagingFlow.collectAsLazyPagingItems(),
                            searchQuery = "",
                            onSearchQueryChanged = {},
                            onMovieClick = { movie ->
                                navController.navigate(Screen.Detail.createRoute(movie.id))
                            }
                        )
                    }
                    composable(
                        route = Screen.Detail.route,
                        arguments = listOf(navArgument(Screen.ARG_MOVIE_ID) { type = NavType.IntType })
                    ) {
                        DetailContent(
                            movieState = TMDBResult.Success(testMovies[0]),
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }

        composeRule.onNodeWithText("Inception").performClick()
        composeRule.onNodeWithText("Overview").assertIsDisplayed()

        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("TMDB").assertIsDisplayed()
    }

    @Test
    fun detailScreenShowsLoadingState() {
        composeRule.setContent {
            TmdbTheme {
                DetailContent(
                    movieState = TMDBResult.Loading,
                    onBackClick = {}
                )
            }
        }

        composeRule.onNodeWithContentDescription("Back").assertIsDisplayed()
    }

    @Test
    fun detailScreenShowsErrorState() {
        composeRule.setContent {
            TmdbTheme {
                DetailContent(
                    movieState = TMDBResult.Error(RuntimeException("Not found")),
                    onBackClick = {}
                )
            }
        }

        composeRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }
}
