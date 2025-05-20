package com.throwaway.movies_take_home.data.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class NetworkMovie(
    @SerialName("id")
    val id: Int,
    @SerialName("genres")
    val genres: List<String>,
    @SerialName("release_date")
    val releaseDate: String,
    @SerialName("title")
    val title: String,
    @SerialName("tagline")
    val tagline: String,
    @SerialName("overview")
    val overview: String,
    @SerialName("url")
    val url: String
)