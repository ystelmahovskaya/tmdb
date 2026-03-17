package com.yuliia.tmdb.domain.useCase

import com.yuliia.tmdb.domain.repository.Repository
import javax.inject.Inject

class ToggleWatchlistUseCase @Inject constructor(
    private val repository: Repository
) {
    suspend operator fun invoke(movieId: Int) {
        repository.toggleWatchlist(movieId)
    }
}
