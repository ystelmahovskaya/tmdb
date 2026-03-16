package com.yuliia.tmdb.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yuliia.tmdb.ui.screens.detail.DetailScreen
import com.yuliia.tmdb.ui.screens.home.HomeScreen

@Composable
fun TMDBNavGraph() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {
        composable(Screen.Home.route) {
            HomeScreen(
                onMovieClick = { movie ->
                    navController.navigate(Screen.Detail.createRoute(movie.id))
                }
            )
        }

        composable(
            route = Screen.Detail.route,
            arguments = listOf(navArgument(Screen.ARG_MOVIE_ID) { type = NavType.IntType })
        ) {
            DetailScreen(onBackClick = { navController.popBackStack() })
        }
    }
}
