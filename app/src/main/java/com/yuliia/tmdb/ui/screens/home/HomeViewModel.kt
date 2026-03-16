package com.yuliia.tmdb.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.domain.useCase.GetTrendingMoviesUseCase
import com.yuliia.tmdb.domain.useCase.SearchMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTrendingMovies: GetTrendingMoviesUseCase,
    private val searchMovies: SearchMoviesUseCase
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    val movies = _searchQuery
        .debounce(SEARCH_DEBOUNCE_MS)
        .flatMapLatest { query ->
            when {
                query.isBlank()                  -> getTrendingMovies()
                query.length < MIN_SEARCH_LENGTH -> flowOf(PagingData.empty())
                else                             -> searchMovies(query)
            }
        }
        .cachedIn(viewModelScope)

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    companion object {
        const val SEARCH_DEBOUNCE_MS = 300L
        const val MIN_SEARCH_LENGTH = 3
    }
}
