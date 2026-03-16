package com.yuliia.tmdb.domain.useCase

import androidx.paging.PagingData
import com.yuliia.tmdb.domain.model.Movie
import com.yuliia.tmdb.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTrendingMoviesUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(): Flow<PagingData<Movie>> {
        return repository.getTrendingMovies()
    }
}
