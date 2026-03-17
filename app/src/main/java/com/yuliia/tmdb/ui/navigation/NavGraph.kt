package com.yuliia.tmdb.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.yuliia.tmdb.R
import com.yuliia.tmdb.ui.screens.detail.DetailScreen
import com.yuliia.tmdb.ui.screens.home.HomeScreen
import com.yuliia.tmdb.ui.screens.watchlist.WatchlistScreen

@Composable
fun TMDBNavGraph() {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    // only show bottom bar on top-level screens
    val showBottomBar = currentDestination?.hierarchy?.any {
        it.route == Screen.Home.route || it.route == Screen.Watchlist.route
    } == true

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Home.route } == true,
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.TrendingUp, contentDescription = null) },
                        label = { Text(stringResource(R.string.trending)) }
                    )
                    NavigationBarItem(
                        selected = currentDestination?.hierarchy?.any { it.route == Screen.Watchlist.route } == true,
                        onClick = {
                            navController.navigate(Screen.Watchlist.route) {
                                popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(Icons.Default.Bookmark, contentDescription = null) },
                        label = { Text(stringResource(R.string.watchlist)) }
                    )
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    onMovieClick = { movie ->
                        navController.navigate(Screen.Detail.createRoute(movie.id))
                    }
                )
            }

            composable(Screen.Watchlist.route) {
                WatchlistScreen(
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
}
