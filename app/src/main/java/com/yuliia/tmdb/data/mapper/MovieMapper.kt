package com.yuliia.tmdb.data.mapper

import com.yuliia.tmdb.data.remote.dto.MovieDetailDto
import com.yuliia.tmdb.data.remote.dto.MovieDto
import com.yuliia.tmdb.data.remote.dto.MovieListResponse
import com.yuliia.tmdb.domain.model.Movie

private const val POSTER_BASE_URL = "https://image.tmdb.org/t/p/w500"
private const val BACKDROP_BASE_URL = "https://image.tmdb.org/t/p/w780"

fun MovieDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath?.let { "$POSTER_BASE_URL$it" },
        backdropPath = backdropPath?.let { "$BACKDROP_BASE_URL$it" },
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

fun MovieDetailDto.toDomain(): Movie {
    return Movie(
        id = id,
        title = title,
        overview = overview,
        posterPath = posterPath?.let { "$POSTER_BASE_URL$it" },
        backdropPath = backdropPath?.let { "$BACKDROP_BASE_URL$it" },
        releaseDate = releaseDate,
        voteAverage = voteAverage,
        voteCount = voteCount,
        popularity = popularity,
        genreIds = genres.map { it.id }
    )
}
