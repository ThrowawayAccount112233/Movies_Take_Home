package com.throwaway.movies_take_home.presentation

import com.throwaway.movies_take_home.data.model.Genre

sealed interface GenresUiState {

    data class Content(
        val genres: List<Genre>
    ) : GenresUiState

    data class Error(
        val fallbackGenres: List<Genre> = listOf(
            Genre.all(10000),
            Genre("Drama", 1000),
            Genre("Action", 2000),
            Genre("Comedy", 3000),
            Genre("Adventure", 4000),
        )
    ) : GenresUiState
}