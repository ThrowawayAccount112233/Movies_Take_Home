package com.throwaway.movies_take_home.data.network

import com.throwaway.movies_take_home.data.network.model.NetworkGenre
import com.throwaway.movies_take_home.data.network.model.NetworkMovie
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * Retrofit API interface for the movie service
 */
interface MovieApiService {

    @GET("/api/genres")
    suspend fun getGenres(): Response<List<NetworkGenre>>

    @GET("/api/movies")
    suspend fun getMovies(
        @Query("limit") limit: Int? = null,
        @Query("from") from: Int? = null,
        @Query("genre") genre: String? = null
    ): Response<List<NetworkMovie>>
}