package com.throwaway.movies_take_home.data.repository

import com.throwaway.movies_take_home.data.di.IO
import com.throwaway.movies_take_home.data.model.Genre
import com.throwaway.movies_take_home.data.model.Movie
import com.throwaway.movies_take_home.data.network.MovieApiService
import com.throwaway.movies_take_home.data.network.model.NetworkGenre
import com.throwaway.movies_take_home.data.network.model.NetworkMovie
import com.throwaway.movies_take_home.data.network.model.Resource
import com.throwaway.movies_take_home.domain.repository.MovieRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Response
import javax.inject.Inject
import javax.inject.Named

/**
 * [MovieRepository] implementation that fetches data from the remote data source, [MovieApiService].
 */
class OnlineMovieRepositoryImpl @Inject constructor(
    private val remoteDataSource: MovieApiService,
    @Named(IO) private val ioDispatcher: CoroutineDispatcher
) : MovieRepository {

    override fun getMovies(limit: Int, from: Int, genre: String?): Flow<Resource<List<Movie>>> {
        return flow {
            val resp = remoteDataSource.getMovies(limit, from, genre)

            if (resp.isSuccessful && resp.body() != null) {
                emit(Resource.success(resp.asMoviesDataModel()))
            } else {
                emit(
                    Resource.error(
                        msg = "Error fetching movies, limit=$limit, from=$from, genre=$genre, " +
                                "resp=${resp.errorBody()}"
                    )
                )
            }
        }.flowOn(ioDispatcher)
    }

    override fun getGenres(): Flow<Resource<List<Genre>>> {
        return flow {
            val resp = remoteDataSource.getGenres()

            if (resp.isSuccessful && resp.body() != null) {
                emit(Resource.success(resp.asGenresDataModel()))
            } else {
                emit(
                    Resource.error(
                        msg = "Error fetching genres, resp=${resp.errorBody()}"
                    )
                )
            }
        }.flowOn(ioDispatcher)
    }
}

private fun Response<List<NetworkMovie>>.asMoviesDataModel() = body()!!.map {
    Movie(
        id = it.id,
        title = it.title,
        releaseDate = it.releaseDate,
        genres = it.genres,
        overview = it.overview,
        url = it.url
    )
}

private fun Response<List<NetworkGenre>>.asGenresDataModel() = body()!!.map {
    Genre(
        name = it.name,
        count = it.count
    )
}