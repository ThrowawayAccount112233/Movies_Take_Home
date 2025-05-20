package com.throwaway.movies_take_home.domain

import com.throwaway.movies_take_home.data.model.Genre
import com.throwaway.movies_take_home.data.network.model.Resource
import com.throwaway.movies_take_home.data.network.model.Status
import com.throwaway.movies_take_home.domain.repository.MovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Domain layer interface for fetching Genres. [invoke]
 */
class GetGenresUseCase @Inject constructor(
    private val repository: MovieRepository,
) {
    operator fun invoke(): Flow<GetGenresResult> {

        return repository.getGenres()
            .mapToDomain()
            .catch {
                println("[DEBUG] Error fetching genres: ${it.localizedMessage}")
                emit(GetGenresResult.Error(it))
            }
    }

    private fun Flow<Resource<List<Genre>>>.mapToDomain() =
        map {
            when (it.status) {
                Status.SUCCESS -> {
                    GetGenresResult.Success(it.data ?: emptyList())
                }

                Status.ERROR -> {
                    GetGenresResult.Error(msg = it.message ?: "Unknown error")
                }
            }
        }

}