package com.yuliia.tmdb.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Watchlist : Screen("watchlist")
    data object Detail : Screen("detail/{$ARG_MOVIE_ID}") {
        fun createRoute(movieId: Int) = "detail/$movieId"
    }

    companion object {
        const val ARG_MOVIE_ID = "movieId"
    }
}
