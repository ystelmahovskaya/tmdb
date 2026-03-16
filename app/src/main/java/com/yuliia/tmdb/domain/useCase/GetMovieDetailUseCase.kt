package com.yuliia.tmdb.domain.useCase

import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.domain.repository.Repository
import com.yuliia.tmdb.util.TMDBResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMovieDetailUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(movieId: Int, forceRefresh: Boolean = false): Flow<TMDBResult<Movie>> {
        return repository.getMovieDetail(movieId, forceRefresh)
    }
}
