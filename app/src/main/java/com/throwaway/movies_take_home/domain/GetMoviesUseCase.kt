package com.throwaway.movies_take_home.domain

import com.throwaway.movies_take_home.data.di.DEFAULT
import com.throwaway.movies_take_home.data.model.Genre
import com.throwaway.movies_take_home.data.model.Movie
import com.throwaway.movies_take_home.data.network.model.Resource
import com.throwaway.movies_take_home.data.network.model.Status
import com.throwaway.movies_take_home.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import com.throwaway.movies_take_home.data.model.isAll
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flowOn
import javax.inject.Named

/**
 * Domain layer interface for fetching movies. The caller must maintain paging state properly to
 * retrieve paged results.
 */
class GetMoviesUseCase @Inject constructor(
    private val repository: MovieRepository,
    @Named(DEFAULT)
    private val dispatcher: CoroutineDispatcher
) {
    operator fun invoke(
        genre: Genre,
        page: Int = INITIAL_PAGE,
        limit: Int = DEFAULT_LIMIT
    ): Flow<GetMoviesResult> {

        return repository.getMovies(
            limit,
            page,
            if (genre.isAll()) null else genre.name
        )
            .mapToDomain()
            .catch {
                println("[DEBUG] Error fetching movies: ${it.localizedMessage}")
                emit(GetMoviesResult.Error(it))
            }.flowOn(dispatcher)
    }

    /**
     * Simply maps Data model to Domain model.
     */
    private fun Flow<Resource<List<Movie>>>.mapToDomain() =
        map {
            when (it.status) {
                Status.SUCCESS -> {
                    GetMoviesResult.Success(it.data ?: emptyList())
                }

                Status.ERROR -> {
                    GetMoviesResult.Error(msg = it.message ?: "Unknown error")
                }
            }
        }
}

const val INITIAL_PAGE = 0
const val DEFAULT_LIMIT = 10