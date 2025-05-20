package com.throwaway.movies_take_home.presentation

sealed interface MoviesUiState {

    object Loading : MoviesUiState

    data class Content(
        val movies: List<MovieRowItem> = emptyList(),
    ) : MoviesUiState {
        fun append(newMovies: List<MovieRowItem>) = copy(movies = movies + newMovies)
    }

    data class Error(val message: String) : MoviesUiState
}