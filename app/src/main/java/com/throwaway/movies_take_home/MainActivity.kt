package com.throwaway.movies_take_home

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.throwaway.movies_take_home.presentation.MoviesScreen
import com.throwaway.movies_take_home.presentation.MoviesViewModel
import com.throwaway.movies_take_home.ui.theme.movies_take_homeTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            movies_take_homeTheme {
                val viewModel: MoviesViewModel by viewModels()
                MoviesScreen(viewModel = viewModel)
            }
        }
    }
}