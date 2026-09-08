package com.soeil.movielist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.soeil.movielist.ui.screen.MovieListScreen
import com.soeil.movielist.ui.theme.Movie_listTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Movie_listTheme {
                MovieListScreen()
            }
        }
    }
}