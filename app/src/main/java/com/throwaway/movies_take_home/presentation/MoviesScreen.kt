package com.throwaway.movies_take_home.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.platform.LocalUriHandler
import com.throwaway.movies_take_home.data.model.Genre

@Composable
fun MoviesScreen(
    viewModel: MoviesViewModel
) {
    val moviesUiState by viewModel.uiState.collectAsStateWithLifecycle()
    val genresUiState by viewModel.genresUiState.collectAsStateWithLifecycle()
    val selectedGenre by viewModel.selectedGenreFlow.collectAsStateWithLifecycle()

    Surface(
        modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            AppHeader()

            GenresSpinner(
                genres = when (val state = genresUiState) {
                    is GenresUiState.Content -> {
                        state.genres
                    }

                    is GenresUiState.Error -> {
                        state.fallbackGenres
                    }
                },
                selectedGenre = selectedGenre,
                onGenreSelected = viewModel::onGenreSelected
            )

            when (val state = moviesUiState) {
                is MoviesUiState.Loading -> LoadingScreen()
                is MoviesUiState.Content -> MoviesContent(state, viewModel::onLoadMoreMovies)
                is MoviesUiState.Error -> ErrorScreen(state.message)
            }
        }
    }
}

@Composable
fun AppHeader() {
    Text(
        text = "Movies App",
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    )
}

@Composable
fun LoadingScreen() {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
    ) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(message: String) {
    Box(
        contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()
    ) {
        Text(
            text = message, color = Color.Red, textAlign = TextAlign.Center
        )
    }
}

@Composable
fun GenresSpinner(
    genres: List<Genre>,
    selectedGenre: Genre,
    onGenreSelected: (Genre) -> Unit
) {
    var expandedDropdown = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable {
                    expandedDropdown.value = !expandedDropdown.value
                }) {
                Text(
                    text = "Select Genre",
                    color = Color.Blue,
                    modifier = Modifier.padding(end = 8.dp)
                )
                Text(text = selectedGenre.name)
            }

            DropdownMenu(
                expanded = expandedDropdown.value,
                onDismissRequest = { expandedDropdown.value = false }) {
                genres.forEach { genre ->
                    DropdownMenuItem(
                        onClick = {
                            onGenreSelected(genre)
                            expandedDropdown.value = false
                        },
                        text = {
                            Row(
                                horizontalArrangement = Arrangement.Start) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = Color.Black,
                                    modifier = Modifier
                                        .padding(end = 8.dp)
                                        .alpha(if (selectedGenre.name == genre.name) 1f else 0f)
                                )
                                Text(genre.name + " (${genre.count})")
                            }
                        },
                    )
                }
            }
        }
    }
}

@Composable
fun MoviesContent(
    state: MoviesUiState.Content,
    onLoadMore: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        MoviesList(
            movies = state.movies,
            onLoadMore
        )
    }
}

@Composable
fun MoviesList(
    movies: List<MovieRowItem>,
    onLoadMore: () -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp)
    ) {
        // No need for key spec since position of items isn't changing (we aren't removing items from
        // the middle of the list). Keyed by position by default.
        itemsIndexed(movies + MovieRowItem.Loading) { index, movie ->
            when (movie) {
                is MovieRowItem.Loading -> {
                    LoadingItem()
                    LaunchedEffect(Unit) {
                        println("[DEBUG] Loading more movies")
                        onLoadMore()
                    }
                }

                is MovieRowItem.Movie -> {
                    MovieItem(movie = movie)
                }
            }

        }
    }
}

@Composable
fun LoadingItem() {
    Box(modifier = Modifier.fillMaxWidth()) {
        CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
    }
}

@Composable
fun MovieItem(movie: MovieRowItem.Movie) {
    val uriHandler = LocalUriHandler.current

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable {
                uriHandler.openUri(movie.url)
            }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Title and year
            Text(
                text = "${movie.title} (${movie.releaseDate})",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Divider
            Divider(color = Color.LightGray)

            Spacer(modifier = Modifier.height(8.dp))

            // Overview
            Text(
                text = movie.overview.toString(), fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Genres
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = movie.genres.joinToString(", "), fontSize = 12.sp, color = Color.DarkGray
                )
            }
        }
    }
}