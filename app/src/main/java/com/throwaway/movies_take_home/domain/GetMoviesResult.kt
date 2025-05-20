package com.throwaway.movies_take_home.domain

import com.throwaway.movies_take_home.data.model.Movie

/**
 * Domain result for movies - Presentation layer handles [Success] or [Error] per UI requirements.
 */
sealed interface GetMoviesResult {

    data class Success(val movies: List<Movie>) : GetMoviesResult
    data class Error(
        val error: Throwable? = null,
        val msg: String? = null
    ) : GetMoviesResult
}