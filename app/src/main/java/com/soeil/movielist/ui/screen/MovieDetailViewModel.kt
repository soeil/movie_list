package com.soeil.movielist.ui.screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soeil.movielist.core.network.OmdbApi
import com.soeil.movielist.core.network.RetrofitClient
import com.soeil.movielist.core.network.model.MovieDetail
import com.soeil.movielist.core.storage.FavoritesStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class MovieDetailUiState(
    val isLoading: Boolean = false,
    val movie: MovieDetail? = null,
    val isLiked: Boolean = false,
    val errorMessage: String? = null,
)

class MovieDetailViewModel(
    application: Application,
    private val imdbId: String,
) : AndroidViewModel(application) {

    private val api = RetrofitClient.create<OmdbApi>()

    private val _uiState = MutableStateFlow(MovieDetailUiState())
    val uiState = _uiState.asStateFlow()

    init {
        val liked = FavoritesStore.getIds(getApplication()).contains(imdbId)
        _uiState.update { it.copy(isLiked = liked) }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching { api.getMovie(imdbId) }
                .onSuccess { detail ->
                    if (detail.Response == "True") {
                        _uiState.update { it.copy(isLoading = false, movie = detail) }
                    } else {
                        _uiState.update { it.copy(isLoading = false, errorMessage = detail.Error ?: "Movie not found") }
                    }
                }
                .onFailure { e ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Network error") }
                }
        }
    }

    fun toggleLike() {
        val liked = FavoritesStore.toggle(getApplication(), imdbId)
        _uiState.update { it.copy(isLiked = liked) }
    }
}
