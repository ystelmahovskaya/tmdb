package com.yuliia.tmdb.ui.screens.watchlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.domain.useCase.GetWatchlistUseCase
import com.yuliia.tmdb.domain.useCase.ToggleWatchlistUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistViewModel @Inject constructor(
    getWatchlist: GetWatchlistUseCase,
    private val toggleWatchlist: ToggleWatchlistUseCase
) : ViewModel() {

    // Room Flow automatically emits when watchlist table changes
    val movies: StateFlow<List<Movie>> = getWatchlist()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun removeFromWatchlist(movieId: Int) {
        viewModelScope.launch { toggleWatchlist(movieId) }
    }
}
