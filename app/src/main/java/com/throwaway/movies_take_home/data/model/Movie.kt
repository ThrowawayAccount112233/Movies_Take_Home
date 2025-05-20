package com.throwaway.movies_take_home.data.model

data class Movie(
    val id: Int,
    val title: String,
    val genres: List<String>,
    val overview: String,
    val releaseDate: String,
    val url: String
)