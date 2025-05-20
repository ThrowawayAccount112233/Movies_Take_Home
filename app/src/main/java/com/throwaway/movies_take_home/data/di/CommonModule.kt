package com.throwaway.movies_take_home.data.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
class CommonModule {

    /**
     * For expensive I/O operations (network calls, etc.)
     */
    @Provides
    @Named(IO)
    fun provideIODispatcher(): CoroutineDispatcher = Dispatchers.IO

    /**
     * For cpu intensive tasks (mapping, etc.)
     */
    @Provides
    @Named(DEFAULT)
    fun provideDefaultDispatcher(): CoroutineDispatcher = Dispatchers.Default
}

const val IO = "IO"
const val DEFAULT = "DEFAULT"