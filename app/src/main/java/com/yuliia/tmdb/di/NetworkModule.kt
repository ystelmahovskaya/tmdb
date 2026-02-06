package com.yuliia.tmdb.di

import com.yuliia.tmdb.data.repository.TMDBApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun providesTMDBApi(): TMDBApi{
        return Retrofit.
        Builder().
        baseUrl("").
        build().
        create(TMDBApi::class.java)
    }
}