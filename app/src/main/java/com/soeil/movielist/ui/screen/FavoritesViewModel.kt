package com.soeil.movielist.ui.screen

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.soeil.movielist.core.network.OmdbApi
import com.soeil.movielist.core.network.RetrofitClient
import com.soeil.movielist.core.network.model.MovieDetail
import com.soeil.movielist.core.storage.FavoritesStore
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class FavoritesUiState(
    val isLoading: Boolean = false,
    val movies: List<MovieDetail> = emptyList(),
    val errorMessage: String? = null,
)

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState = _uiState.asStateFlow()

    init {
        refresh()
    }

    /** Re-reads liked ids from storage and fetches each movie's details. */
    fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val ids = FavoritesStore.getIds(getApplication())
            if (ids.isEmpty()) {
                _uiState.update { it.copy(isLoading = false, movies = emptyList()) }
                return@launch
            }
            runCatching {
                coroutineScope {
                    // Fetch all liked movies in parallel.
                    ids.map { id -> async { runCatching { RetrofitClient.create<OmdbApi>().getMovie(id) }.getOrNull() } }
                        .awaitAll()
                }
            }.onSuccess { details ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        movies = details.filter { d -> d?.Response == "True" }.mapNotNull { d -> d },
                    )
                }
            }.onFailure { e ->
                _uiState.update { it.copy(isLoading = false, errorMessage = e.message ?: "Network error") }
            }
        }
    }
}
