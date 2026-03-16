package com.yuliia.tmdb

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.yuliia.tmdb.ui.navigation.TMDBNavGraph
import com.yuliia.tmdb.ui.theme.TmdbTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TmdbTheme {
                TMDBNavGraph()
            }
        }
    }
}
