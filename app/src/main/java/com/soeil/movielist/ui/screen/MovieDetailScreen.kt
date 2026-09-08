package com.soeil.movielist.ui.screen

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.soeil.movielist.core.network.model.MovieDetail

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    imdbId: String,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val app = LocalContext.current.applicationContext as Application
    val viewModel: MovieDetailViewModel = viewModel { MovieDetailViewModel(app, imdbId) }
    val uiState by viewModel.uiState.collectAsState()
    val movie = uiState.movie

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(movie?.Title ?: "Movie") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.errorMessage != null -> Text(
                    text = uiState.errorMessage!!,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .padding(24.dp),
                    textAlign = TextAlign.Center,
                )

                movie == null -> CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )

                else -> Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = movie.Poster,
                            contentDescription = movie.Title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .aspectRatio(2f / 3f)
                                .background(MaterialTheme.colorScheme.surfaceVariant),
                            contentScale = ContentScale.Crop,
                        )
                        FilledIconButton(
                            onClick = viewModel::toggleLike,
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(end = 16.dp)
                                .offset(y = 24.dp), // half of the 48dp button hangs below the poster
                        ) {
                            Icon(
                                imageVector = if (uiState.isLiked) {
                                    Icons.Filled.Favorite
                                } else {
                                    Icons.Filled.FavoriteBorder
                                },
                                contentDescription = if (uiState.isLiked) "Unlike" else "Like",
                            )
                        }
                    }
                    Column(
                        modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 44.dp, bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        movie.Title?.let { title ->
                            Text(text = title, style = MaterialTheme.typography.headlineSmall)
                        }
                        DetailSection(movie)
                    }
                }
            }
        }
    }
}

@Composable
private fun DetailSection(movie: MovieDetail) {
    // (label, value) pairs shown only when the API actually has the data.
    // ponytail: fields kept as pairs; split into a model-backed formatter when more UI grows
    val rows = listOf(
        "Released" to movie.Released,
        "Runtime" to movie.Runtime,
        "Plot" to movie.Plot,
        "Genre" to movie.Genre,
        "Director" to movie.Director,
        "Writer" to movie.Writer,
        "Actors" to movie.Actors,
        "Language" to movie.Language,
        "Country" to movie.Country,
        "Awards" to movie.Awards,
    )

    rows.forEach { (label, value) ->
        value?.takeUnless { it.isNA() }?.let { v ->
            Row(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = label,
                    modifier = Modifier.width(100.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = v, style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}

// OMDB fills gaps with the literal string "N/A".
private fun String.isNA(): Boolean = trim().equals("N/A", ignoreCase = true)
