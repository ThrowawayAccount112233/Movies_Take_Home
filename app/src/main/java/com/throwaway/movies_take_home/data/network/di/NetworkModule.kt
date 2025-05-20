package com.throwaway.movies_take_home.data.network.di

import com.throwaway.movies_take_home.data.network.MovieApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import retrofit2.Converter
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import javax.inject.Singleton

/**
 * Network module to provide Retrofit and API service instances
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val BASE_URL = "https://redacted.com"

    @Provides
    @Singleton
    fun providesMoviesApiService(converterFactory: Converter.Factory): MovieApiService {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(converterFactory)
            .build()
            .create(MovieApiService::class.java)
    }

    @Provides
    fun providesConverterFactory() =
        Json.asConverterFactory("application/json".toMediaType())
}