package com.yuliia.tmdb.domain.useCase

import com.yuliia.tmdb.domain.repository.Repository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class IsWatchlistedUseCase @Inject constructor(
    private val repository: Repository
) {
    operator fun invoke(movieId: Int): Flow<Boolean> {
        return repository.isWatchlisted(movieId)
    }
}
