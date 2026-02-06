package com.yuliia.tmdb.domain.repository


interface Repository {
    suspend fun doNetworkCall()
}