package com.yuliia.tmdb.domain.useCase

import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetWatchlistUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(): Flow<List<Movie>> {
        return repository.getWatchlist()
    }
}
