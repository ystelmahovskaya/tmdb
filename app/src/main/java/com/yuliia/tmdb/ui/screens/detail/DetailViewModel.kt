package com.yuliia.tmdb.ui.screens.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.domain.useCase.GetMovieDetailUseCase
import com.yuliia.tmdb.domain.useCase.IsWatchlistedUseCase
import com.yuliia.tmdb.domain.useCase.ToggleWatchlistUseCase
import com.yuliia.tmdb.ui.navigation.Screen
import com.yuliia.tmdb.util.TMDBResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMovieDetail: GetMovieDetailUseCase,
    private val isWatchlistedUseCase: IsWatchlistedUseCase,
    private val toggleWatchlistUseCase: ToggleWatchlistUseCase
) : ViewModel() {

    private val movieId: Int? = savedStateHandle.get<Int>(Screen.ARG_MOVIE_ID)

    private val _movie = MutableStateFlow<TMDBResult<Movie>>(TMDBResult.Loading)
    val movie: StateFlow<TMDBResult<Movie>> = _movie

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing

    // Room Flow — auto-updates when watchlist changes from any screen
    val isWatchlisted: StateFlow<Boolean> = movieId?.let { id ->
        isWatchlistedUseCase(id).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)
    } ?: MutableStateFlow(false)

    private var loadJob: Job? = null

    init {
        if (movieId == null) {
            _movie.value = TMDBResult.Error(IllegalArgumentException("Movie not found"))
        } else {
            loadMovie(movieId, forceRefresh = false)
        }
    }

    // retry replaces the whole screen with Loading state (via loadMovie -> Loading emission)
    fun retry() {
        movieId?.let { loadMovie(it, forceRefresh = true) }
    }

    // refresh shows the pull-to-refresh indicator instead of replacing content
    fun refresh() {
        _isRefreshing.value = true
        movieId?.let { loadMovie(it, forceRefresh = true) }
    }

    fun toggleWatchlist() {
        movieId?.let { id ->
            viewModelScope.launch { toggleWatchlistUseCase(id) }
        }
    }

    private fun loadMovie(id: Int, forceRefresh: Boolean) {
        loadJob?.cancel()
        loadJob = getMovieDetail(id, forceRefresh)
            .onEach { result ->
                _movie.value = result
                if (result !is TMDBResult.Loading) {
                    _isRefreshing.value = false
                }
            }
            .launchIn(viewModelScope)
    }
}
