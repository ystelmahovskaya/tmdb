package com.yuliia.tmdb.data.repository

import retrofit2.http.GET


interface TMDBApi {

    @GET("test")
    suspend fun doNetworkCall()
}