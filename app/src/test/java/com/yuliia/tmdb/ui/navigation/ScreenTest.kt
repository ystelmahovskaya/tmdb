package com.yuliia.tmdb.ui.navigation

import org.junit.Assert.assertEquals
import org.junit.Test

class ScreenTest {

    @Test
    fun `Home route is home`() {
        assertEquals("home", Screen.Home.route)
    }

    @Test
    fun `Detail route contains movieId placeholder`() {
        assertEquals("detail/{movieId}", Screen.Detail.route)
    }

    @Test
    fun `Detail createRoute builds correct path`() {
        assertEquals("detail/42", Screen.Detail.createRoute(42))
    }

    @Test
    fun `Detail createRoute handles zero id`() {
        assertEquals("detail/0", Screen.Detail.createRoute(0))
    }
}
