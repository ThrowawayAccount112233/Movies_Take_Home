package com.throwaway.movies_take_home.domain.repository

import com.throwaway.movies_take_home.data.model.Genre
import com.throwaway.movies_take_home.data.model.Movie
import com.throwaway.movies_take_home.data.network.model.Resource
import kotlinx.coroutines.flow.Flow

/**
 * Following Clean Architecture principles, this sits in the Domain layer. In a properly
 * modularized app, the data module would implement this interface so as to abstract
 * the data source from the domain layer.
 */
interface MovieRepository {

    fun getMovies(limit: Int, from: Int, genre: String?): Flow<Resource<List<Movie>>>

    fun getGenres(): Flow<Resource<List<Genre>>>
}