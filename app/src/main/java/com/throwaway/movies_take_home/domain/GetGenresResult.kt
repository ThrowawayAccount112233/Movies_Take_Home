package com.throwaway.movies_take_home.domain

import com.throwaway.movies_take_home.data.model.Genre

/**
 * Domain result for Genres - Presentation layer handles [Success] or [Error] per UI requirements.
 */
sealed interface GetGenresResult {

    data class Success(val genres: List<Genre>) : GetGenresResult
    data class Error(
        val error: Throwable? = null,
        val msg: String? = null
    ) : GetGenresResult
}