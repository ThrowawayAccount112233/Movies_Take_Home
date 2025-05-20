package com.throwaway.movies_take_home.domain.usecase

import com.throwaway.movies_take_home.data.model.ALL
import com.throwaway.movies_take_home.data.model.Genre
import com.throwaway.movies_take_home.data.model.Movie
import com.throwaway.movies_take_home.data.network.model.Resource
import com.throwaway.movies_take_home.data.network.model.Status
import com.throwaway.movies_take_home.domain.DEFAULT_LIMIT
import com.throwaway.movies_take_home.domain.GetMoviesResult
import com.throwaway.movies_take_home.domain.GetMoviesUseCase
import com.throwaway.movies_take_home.domain.INITIAL_PAGE
import com.throwaway.movies_take_home.domain.repository.MovieRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert
import org.junit.Test
import org.mockito.Mockito

@ExperimentalCoroutinesApi
class GetMoviesUseCaseTest {

    private val repo = Mockito.mock<MovieRepository>()
    private val testDispatcher = StandardTestDispatcher().also {
        Dispatchers.setMain(it)
    }
    private val useCase = GetMoviesUseCase(repo, testDispatcher)

    @Test
    fun `invoke returns Success with movies when repository returns success`() = runTest {
        // Given
        val testGenre = Genre("Action", 5)
        val testMovies = listOf(
            Movie(
                id = 1,
                genres = listOf("Action"),
                releaseDate = "2023-01-01",
                title = "Test Movie",
                overview = "Test Overview",
                url = "http://example.com/movie/1"
            )
        )

        Mockito.`when`(repo.getMovies(limit = 30, from = 8, testGenre.name)).thenReturn(
            flowOf(Resource.Companion.success(testMovies))
        )

        // When
        val result = useCase(testGenre, limit = 30, page = 8).first()

        // Then
        Assert.assertTrue(result is GetMoviesResult.Success)
        Assert.assertEquals(testMovies, (result as GetMoviesResult.Success).movies)
        Mockito.verify(repo, Mockito.times(1)).getMovies(limit = 30, from = 8, testGenre.name)
    }

    @Test
    fun `invoke returns Success with empty list when repository returns success with null data`() =
        runTest {
            // Given
            val testGenre = Genre("Action", 5)

            Mockito.`when`(repo.getMovies(DEFAULT_LIMIT, INITIAL_PAGE, testGenre.name)).thenReturn(
                flowOf(Resource(Status.SUCCESS, null, null))
            )

            // When
            val result = useCase(testGenre).first()

            // Then
            Assert.assertTrue(result is GetMoviesResult.Success)
            Assert.assertEquals(emptyList<Movie>(), (result as GetMoviesResult.Success).movies)
            Mockito.verify(repo, Mockito.times(1))
                .getMovies(DEFAULT_LIMIT, INITIAL_PAGE, testGenre.name)
        }

    @Test
    fun `invoke returns Error when repository returns error`() = runTest {
        // Given
        val testGenre = Genre("Action", 5)
        val errorMessage = "Network error"

        Mockito.`when`(repo.getMovies(DEFAULT_LIMIT, INITIAL_PAGE, testGenre.name)).thenReturn(
            flowOf(Resource.Companion.error(errorMessage))
        )

        // When
        val result = useCase(testGenre).first()

        // Then
        Assert.assertTrue(result is GetMoviesResult.Error)
        Assert.assertEquals(errorMessage, (result as GetMoviesResult.Error).msg)
        Mockito.verify(repo, Mockito.times(1))
            .getMovies(DEFAULT_LIMIT, INITIAL_PAGE, testGenre.name)
    }

    @Test
    fun `invoke returns Error with unknown error message when repository returns error with null message`() =
        runTest {
            // Given
            val testGenre = Genre("Action", 5)

            Mockito.`when`(repo.getMovies(DEFAULT_LIMIT, INITIAL_PAGE, testGenre.name)).thenReturn(
                flowOf(Resource(Status.ERROR, null, null))
            )

            // When
            val result = useCase(testGenre).first()

            // Then
            Assert.assertTrue(result is GetMoviesResult.Error)
            Assert.assertEquals("Unknown error", (result as GetMoviesResult.Error).msg)
            Mockito.verify(repo, Mockito.times(1))
                .getMovies(DEFAULT_LIMIT, INITIAL_PAGE, testGenre.name)
        }

    @Test
    fun `invoke passes null genre to repository when ALL genre is provided`() = runTest {
        // Given
        val allGenre = Genre(ALL, 10)
        val testMovies = listOf(
            Movie(
                id = 1,
                genres = listOf("Action", "Adventure"),
                releaseDate = "2023-01-01",
                title = "Test Movie",
                overview = "Test Overview",
                url = "http://example.com/movie/1"
            )
        )

        Mockito.`when`(repo.getMovies(DEFAULT_LIMIT, INITIAL_PAGE, null)).thenReturn(
            flowOf(Resource.Companion.success(testMovies))
        )

        // When
        val result = useCase(allGenre).first()

        // Then
        Assert.assertTrue(result is GetMoviesResult.Success)
        Assert.assertEquals(testMovies, (result as GetMoviesResult.Success).movies)
        Mockito.verify(repo, Mockito.times(1)).getMovies(DEFAULT_LIMIT, INITIAL_PAGE, null)
    }

    @Test
    fun `invoke uses custom page and limit values when provided`() = runTest {
        // Given
        val testGenre = Genre("Action", 5)
        val customPage = 2
        val customLimit = 20
        val testMovies = listOf(
            Movie(
                id = 1,
                genres = listOf("Action"),
                releaseDate = "2023-01-01",
                title = "Test Movie",
                overview = "Test Overview",
                url = "http://example.com/movie/1"
            )
        )

        Mockito.`when`(repo.getMovies(customLimit, customPage, testGenre.name)).thenReturn(
            flowOf(Resource.Companion.success(testMovies))
        )

        // When
        val result = useCase(testGenre, customPage, customLimit).first()

        // Then
        Assert.assertTrue(result is GetMoviesResult.Success)
        Assert.assertEquals(testMovies, (result as GetMoviesResult.Success).movies)
        Mockito.verify(repo, Mockito.times(1)).getMovies(customLimit, customPage, testGenre.name)
    }
}