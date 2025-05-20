package com.throwaway.movies_take_home.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.throwaway.movies_take_home.data.di.DEFAULT
import com.throwaway.movies_take_home.data.model.Genre
import com.throwaway.movies_take_home.domain.GetGenresUseCase
import com.throwaway.movies_take_home.domain.GetMoviesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class MoviesViewModel @Inject constructor(
    private val getMoviesUseCase: GetMoviesUseCase,
    getGenresUseCase: GetGenresUseCase,
    private val mapper: MoviesUiStateMapper,
    @Named(DEFAULT) private val dispatcher: CoroutineDispatcher
) : ViewModel() {

    val selectedGenreFlow = MutableStateFlow<Genre>(Genre.all())
    private val pageFlow = MutableStateFlow<Int>(INITIAL_PAGE)

    private var moviesUiState = INITIAL_MOVIES_UI_STATE

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<MoviesUiState> =
        combine(selectedGenreFlow, pageFlow) { selectedGenre, page ->
            GetMoviesInput(page, selectedGenre)
        }.flatMapLatest { input ->
            println("[DEBUG] New input: Selected Genre: page=${input.page}, genre=${input.genre.name}")
            getMoviesUseCase(input.genre, input.page)
        }.map { domainResult ->
            val nextUiState = mapper.mapToUi(domainResult)

            when (nextUiState) {
                // If we receive more content, just append it to the existing state.
                is MoviesUiState.Content -> {
                    println("[DEBUG] Appending state: $nextUiState")
                    moviesUiState = moviesUiState.append(nextUiState.movies)
                    moviesUiState
                }

                // Otherwise just pass through non-content state
                else -> {
                    println("[DEBUG] Passing through state: $nextUiState")
                    nextUiState
                }
            }
        }.flowOn(dispatcher)
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(5000),
                MoviesUiState.Loading
            )

    val genresUiState = getGenresUseCase()
        .map { domainResult ->
            mapper.mapToUi(domainResult)
        }
        .flowOn(dispatcher)
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(5000),
            GenresUiState.Content(emptyList<Genre>())
        )

    fun onGenreSelected(genre: Genre) {
        // Do nothing if we already have this genre selected
        if (selectedGenreFlow.value.name == genre.name) return

        onGenreChanged(genre)

    }

    fun onLoadMoreMovies() {
        val nextPage = pageFlow.value + 1
        pageFlow.tryEmit(nextPage)
    }

    private fun onGenreChanged(genre: Genre) {
        // Reset cached UI state because we want changing genres to re-load the entire list
        moviesUiState = INITIAL_MOVIES_UI_STATE

        // Reset page because we changed genres
        pageFlow.tryEmit(INITIAL_PAGE)

        // Lastly, emit the new genre
        selectedGenreFlow.tryEmit(genre)
    }

    inner class GetMoviesInput(val page: Int, val genre: Genre)
}

const val INITIAL_PAGE = 0
val INITIAL_MOVIES_UI_STATE = MoviesUiState.Content(emptyList())