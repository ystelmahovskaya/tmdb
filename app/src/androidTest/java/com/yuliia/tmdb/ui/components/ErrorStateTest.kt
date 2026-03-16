package com.yuliia.tmdb.ui.components

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.yuliia.tmdb.ui.theme.TmdbTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class ErrorStateTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun displaysErrorIcon() {
        composeRule.setContent {
            TmdbTheme { ErrorState(message = "Error occurred") }
        }

        composeRule.onNodeWithContentDescription("Error").assertIsDisplayed()
    }

    @Test
    fun displaysErrorMessage() {
        composeRule.setContent {
            TmdbTheme { ErrorState(message = "Something went wrong") }
        }

        composeRule.onNodeWithText("Something went wrong").assertIsDisplayed()
    }

    @Test
    fun hidesRetryButtonWhenNoCallback() {
        composeRule.setContent {
            TmdbTheme { ErrorState(message = "Error", onRetry = null) }
        }

        composeRule.onNodeWithText("Try again").assertDoesNotExist()
    }

    @Test
    fun displaysRetryButtonWhenCallbackProvided() {
        composeRule.setContent {
            TmdbTheme { ErrorState(message = "Error", onRetry = {}) }
        }

        composeRule.onNodeWithText("Try again").assertIsDisplayed()
    }

    @Test
    fun retryButtonCallsCallback() {
        var clicked = false
        composeRule.setContent {
            TmdbTheme { ErrorState(message = "Error", onRetry = { clicked = true }) }
        }

        composeRule.onNodeWithText("Try again").performClick()
        assertTrue(clicked)
    }
}
