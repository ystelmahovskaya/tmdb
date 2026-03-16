package com.yuliia.tmdb

import com.yuliia.tmdb.data.remote.dto.GenreDto
import com.yuliia.tmdb.data.remote.dto.MovieDetailDto
import com.yuliia.tmdb.data.remote.dto.MovieDto
import com.yuliia.tmdb.data.remote.dto.MovieListResponse
import com.yuliia.tmdb.data.remote.dto.VideoResponse
import com.yuliia.tmdb.domain.model.Movie

object TestData {

    fun movie(
        id: Int = 1,
        title: String = "Test Movie"
    ) = Movie(
        id = id,
        title = title,
        overview = "Test overview",
        posterPath = "https://image.tmdb.org/t/p/w500/poster.jpg",
        backdropPath = "https://image.tmdb.org/t/p/w780/backdrop.jpg",
        releaseDate = "2025-01-01",
        voteAverage = 7.5,
        voteCount = 100,
        popularity = 50.0,
        genreIds = listOf(28, 12)
    )

    fun movieDto(
        id: Int = 1,
        title: String = "Test Movie"
    ) = MovieDto(
        id = id,
        adult = false,
        backdropPath = "/backdrop.jpg",
        title = title,
        originalLanguage = "en",
        originalTitle = title,
        overview = "Test overview",
        posterPath = "/poster.jpg",
        mediaType = "movie",
        genreIds = listOf(28, 12),
        popularity = 50.0,
        releaseDate = "2025-01-01",
        video = false,
        voteAverage = 7.5,
        voteCount = 100
    )

    fun movieDetailDto(
        id: Int = 1,
        title: String = "Test Movie"
    ) = MovieDetailDto(
        id = id,
        backdropPath = "/backdrop.jpg",
        title = title,
        overview = "Test overview",
        posterPath = "/poster.jpg",
        genres = listOf(GenreDto(28, "Action"), GenreDto(12, "Adventure")),
        popularity = 50.0,
        releaseDate = "2025-01-01",
        voteAverage = 7.5,
        voteCount = 100
    )

    fun emptyVideoResponse() = VideoResponse(results = emptyList())

    fun movieListResponse(movies: List<MovieDto> = listOf(movieDto())) = MovieListResponse(
        page = 1,
        results = movies,
        totalPages = 1,
        totalResults = movies.size
    )
}
