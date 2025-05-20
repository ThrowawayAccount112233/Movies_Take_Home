package com.throwaway.movies_take_home.presentation

/**
 * Represents a row item in the movie list on the UI.
 */
sealed interface MovieRowItem {
    data class Movie(
        val id: Int,
        val title: String,
        val genres: List<String>,
        val overview: String,
        val releaseDate: String,
        val url: String
    ) : MovieRowItem

    object Loading : MovieRowItem
}