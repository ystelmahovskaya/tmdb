package com.yuliia.tmdb.data.repository

import android.app.Application
import com.yuliia.tmdb.domain.repository.Repository
import javax.inject.Inject

class RepositoryImpl @Inject constructor(
    private val api: TMDBApi,
): Repository {

    override suspend fun getTrendingMovies() {

    }
}