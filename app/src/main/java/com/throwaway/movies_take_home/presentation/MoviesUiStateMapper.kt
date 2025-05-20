package com.throwaway.movies_take_home.presentation

import com.throwaway.movies_take_home.data.model.Genre
import com.throwaway.movies_take_home.domain.GetGenresResult
import com.throwaway.movies_take_home.domain.GetMoviesResult
import javax.inject.Inject

/**
 * Maps domain layer state to presentation layer state.
 */
class MoviesUiStateMapper @Inject constructor() {

    fun mapToUi(
        domainResult: GetMoviesResult
    ): MoviesUiState {
        return when (domainResult) {
            is GetMoviesResult.Error -> {
                MoviesUiState.Error("There was an error.")
            }

            is GetMoviesResult.Success -> {
                MoviesUiState.Content(
                    domainResult.movies.map { movie ->
                        MovieRowItem.Movie(
                            id = movie.id,
                            title = movie.title,
                            overview = movie.overview,
                            releaseDate = movie.releaseDate,
                            genres = movie.genres,
                            url = movie.url
                        )
                    }
                )
            }
        }
    }

    fun mapToUi(domainResult: GetGenresResult): GenresUiState {
        return when (domainResult) {
            is GetGenresResult.Error -> {
                GenresUiState.Error()
            }

            is GetGenresResult.Success -> {
                if (domainResult.genres.isEmpty()) {
                    GenresUiState.Error()
                } else {
                    GenresUiState.Content(
                        genres = listOf(Genre.all(domainResult.genres.sumOf { it.count })) + domainResult.genres
                    )
                }
            }
        }
    }
}