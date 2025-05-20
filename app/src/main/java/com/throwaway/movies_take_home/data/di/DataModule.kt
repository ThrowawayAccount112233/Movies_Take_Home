package com.throwaway.movies_take_home.data.di

import com.throwaway.movies_take_home.data.repository.OnlineMovieRepositoryImpl
import com.throwaway.movies_take_home.domain.repository.MovieRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
interface DataModule {

    @Binds
    @Singleton
    fun bindsMovieRepository(repo: OnlineMovieRepositoryImpl): MovieRepository
}