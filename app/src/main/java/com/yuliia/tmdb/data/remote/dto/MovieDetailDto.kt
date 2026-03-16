package com.yuliia.tmdb.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MovieDetailDto(
    @SerialName("id") val id: Int,
    @SerialName("backdrop_path") val backdropPath: String?,
    @SerialName("title") val title: String,
    @SerialName("overview") val overview: String = "",
    @SerialName("poster_path") val posterPath: String?,
    @SerialName("genres") val genres: List<GenreDto> = emptyList(),
    @SerialName("popularity") val popularity: Double = 0.0,
    @SerialName("release_date") val releaseDate: String = "",
    @SerialName("vote_average") val voteAverage: Double = 0.0,
    @SerialName("vote_count") val voteCount: Int = 0
)

@Serializable
data class GenreDto(
    @SerialName("id") val id: Int,
    @SerialName("name") val name: String
)
