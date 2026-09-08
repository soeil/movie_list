package com.soeil.movielist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.soeil.movielist.ui.screen.FavoritesScreen
import com.soeil.movielist.ui.screen.MovieDetailScreen
import com.soeil.movielist.ui.screen.MovieListScreen
import com.soeil.movielist.ui.theme.Movie_listTheme

private sealed interface Screen {
    data object Home : Screen
    data object Favorites : Screen
    data class Detail(val imdbId: String, val backTo: Screen) : Screen
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Movie_listTheme {
                // ponytail: plain state routing instead of navigation-compose until more screens exist
                var screen by remember { mutableStateOf<Screen>(Screen.Home) }

                when (val current = screen) {
                    Screen.Home -> MovieListScreen(
                        onMovieClick = { screen = Screen.Detail(it, backTo = Screen.Home) },
                        onFavoritesClick = { screen = Screen.Favorites },
                    )

                    Screen.Favorites -> FavoritesScreen(
                        onMovieClick = { screen = Screen.Detail(it, backTo = Screen.Favorites) },
                        onBack = { screen = Screen.Home },
                    )

                    is Screen.Detail -> MovieDetailScreen(
                        imdbId = current.imdbId,
                        onBack = { screen = current.backTo },
                    )
                }
            }
        }
    }
}
