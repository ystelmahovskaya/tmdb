package com.yuliia.tmdb.data.repository

import com.yuliia.tmdb.data.remote.dto.MovieListResponse
import retrofit2.http.GET
import retrofit2.http.Path


interface TMDBApi {

    @GET("trending/movie/{time_window}")
    suspend fun getTrendingMovies(@Path ("time_window") timeWindow: String): MovieListResponse
}