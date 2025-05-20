package com.throwaway.movies_take_home.data.repository

import com.throwaway.movies_take_home.data.model.Genre
import com.throwaway.movies_take_home.data.model.Movie
import com.throwaway.movies_take_home.data.network.MovieApiService
import com.throwaway.movies_take_home.data.network.model.NetworkGenre
import com.throwaway.movies_take_home.data.network.model.NetworkMovie
import com.throwaway.movies_take_home.data.network.model.Status
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.times
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import retrofit2.Response

@ExperimentalCoroutinesApi
class OnlineMovieRepositoryImplTest {

    private val mockApiService = mock<MovieApiService>()
    private val testDispatcher = StandardTestDispatcher().also {
        Dispatchers.setMain(it)
    }
    private val repository = OnlineMovieRepositoryImpl(mockApiService, testDispatcher)

    @Test
    fun `getMovies returns success response when API call is successful`() = runTest {
        // Given
        val networkMovies = listOf(
            NetworkMovie(
                id = 1,
                genres = listOf("Action", "Adventure"),
                releaseDate = "2023-01-01",
                title = "Test Movie",
                tagline = "Test Tagline",
                overview = "Test Overview",
                url = "http://example.com/movie/1"
            )
        )
        val expectedMovies = listOf(
            Movie(
                id = 1,
                genres = listOf("Action", "Adventure"),
                releaseDate = "2023-01-01",
                title = "Test Movie",
                overview = "Test Overview",
                url = "http://example.com/movie/1"
            )
        )

        `when`(mockApiService.getMovies(10, 0, "Action")).thenReturn(
            Response.success(networkMovies)
        )

        // When
        val result = repository.getMovies(10, 0, "Action").first()

        // Then
        assertEquals(Status.SUCCESS, result.status)
        assertEquals(expectedMovies, result.data)
        assertNull(result.message)
        verify(mockApiService, times(1)).getMovies(10, 0, "Action")
    }

    @Test
    fun `getMovies returns error response when API call fails`() = runTest {
        val errorResponse = Response.error<List<NetworkMovie>>(
            404,
            "Not found".toResponseBody("text/plain".toMediaTypeOrNull())
        )

        `when`(mockApiService.getMovies(10, 0, null)).thenReturn(errorResponse)

        val result = repository.getMovies(10, 0, null).first()

        assertEquals(Status.ERROR, result.status)
        assertNull(result.data)
        verify(mockApiService, times(1)).getMovies(10, 0, null)
    }

    @Test
    fun `getGenres returns success response when API call is successful`() = runTest {
        // Given
        val networkGenres = listOf(
            NetworkGenre("Drama", 10),
            NetworkGenre("Comedy", 15)
        )
        val expectedGenres = listOf(
            Genre("Drama", 10),
            Genre("Comedy", 15)
        )

        `when`(mockApiService.getGenres()).thenReturn(
            Response.success(networkGenres)
        )

        // When
        val result = repository.getGenres().first()

        // Then
        assertEquals(Status.SUCCESS, result.status)
        assertEquals(expectedGenres, result.data)
        assertNull(result.message)
        verify(mockApiService, times(1)).getGenres()
    }

    @Test
    fun `getGenres returns error response when API call fails`() = runTest {
        val errorResponse = Response.error<List<NetworkGenre>>(
            500,
            "Server error".toResponseBody("text/plain".toMediaTypeOrNull())
        )

        `when`(mockApiService.getGenres()).thenReturn(errorResponse)

        val result = repository.getGenres().first()

        assertEquals(Status.ERROR, result.status)
        assertNull(result.data)
        verify(mockApiService, times(1)).getGenres()
    }
}