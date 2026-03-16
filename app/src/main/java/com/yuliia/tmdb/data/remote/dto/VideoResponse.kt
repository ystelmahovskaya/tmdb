package com.yuliia.tmdb.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VideoResponse(
    @SerialName("results") val results: List<VideoDto>
)

@Serializable
data class VideoDto(
    @SerialName("key") val key: String,
    @SerialName("site") val site: String,
    @SerialName("type") val type: String,
    @SerialName("official") val official: Boolean = false
)
