package com.yuliia.tmdb.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yuliia.tmdb.domain.model.Movie

@Entity(tableName = "movies")
data class MovieEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val overview: String,
    val posterPath: String?,
    val backdropPath: String?,
    val releaseDate: String,
    val voteAverage: Double,
    val voteCount: Int,
    val popularity: Double,
    val genreIds: String,
    val trailerUrl: String? = null,
    val isTrending: Boolean = false,
    val page: Int = 0
)

fun MovieEntity.toDomain(): Movie = Movie(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount,
    popularity = popularity,
    genreIds = if (genreIds.isBlank()) emptyList() else genreIds.split(",").map { it.toInt() },
    trailerUrl = trailerUrl
)

fun Movie.toEntity(isTrending: Boolean = false, page: Int = 0): MovieEntity = MovieEntity(
    id = id,
    title = title,
    overview = overview,
    posterPath = posterPath,
    backdropPath = backdropPath,
    releaseDate = releaseDate,
    voteAverage = voteAverage,
    voteCount = voteCount,
    popularity = popularity,
    genreIds = genreIds.joinToString(","),
    trailerUrl = trailerUrl,
    isTrending = isTrending,
    page = page
)
