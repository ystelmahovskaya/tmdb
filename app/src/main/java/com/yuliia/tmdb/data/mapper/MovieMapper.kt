package com.yuliia.tmdb.data.mapper

import com.yuliia.tmdb.data.remote.dto.MovieDto
import com.yuliia.tmdb.data.remote.dto.MovieListResponse
import com.yuliia.tmdb.domain.model.Movie

fun MovieDto.toDomain(): Movie {
        return Movie(
            id = id,
            title = title,
            overview = overview,
            posterPath = posterPath?.let { "https://image.tmdb.org/t/p/w500$it" },
            backdropPath = backdropPath?.let { "https://image.tmdb.org/t/p/w780$it" },
            releaseDate = releaseDate,
            voteAverage = voteAverage,
            voteCount = voteCount,
            popularity = popularity,
            genreIds = genreIds
        )
    }

    fun MovieListResponse.toDomain(): List<Movie> {
        return results.map { it.toDomain() }
    }