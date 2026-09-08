package com.soeil.movielist.ui.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.soeil.movielist.core.network.OmdbApi
import com.soeil.movielist.core.network.RetrofitClient
import com.soeil.movielist.core.network.model.Movie
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

// OMDB has no browse-all endpoint; this is the initial list until the user searches.
private const val DEFAULT_QUERY = "guardians"

data class MovieListUiState(
    val query: String = DEFAULT_QUERY,
    val movies: List<Movie> = emptyList(),
    val page: Int = 1,
    val errorMessage: String? = null,
    val isLoading: Boolean = false,
    val isEnd: Boolean = false,
)

class MovieListViewModel : ViewModel() {

    private val api = RetrofitClient.create<OmdbApi>()

    private val _uiState = MutableStateFlow(MovieListUiState())
    val uiState = _uiState.asStateFlow()

    init {
        fetch(query = DEFAULT_QUERY, page = 1)
    }

    fun search(query: String) {
        val q = query.trim()
        if (q.isEmpty()) return
        _uiState.value = MovieListUiState(query = q)
        fetch(q, page = 1)
    }

    fun loadMore() {
        val s = _uiState.value
        if (s.isLoading || s.isEnd) return
        fetch(s.query, s.page + 1)
    }

    private fun fetch(query: String, page: Int) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            runCatching { api.searchMovies(query = query, page = page) }
                .onSuccess { response ->
                    val found = response.Search.orEmpty()
                    _uiState.update { s ->
                        // Drop responses from an older query (e.g. slow page-2
                        // arriving after the user searched something else).
                        if (s.query != query) return@update s
                        when {
                            found.isEmpty() -> s.copy(
                                isLoading = false,
                                isEnd = true,
                                errorMessage = if (page == 1) response.Error ?: "No movies found" else s.errorMessage,
                            )
                            // OMDB caps a page at 10; a short page means the last one.
                            else -> s.copy(
                                isLoading = false,
                                movies = s.movies + found,
                                isEnd = found.size < 10, // ponytail: no totalResults parsing needed
                            )
                        }
                    }
                }
                .onFailure { e ->
                    _uiState.update { s ->
                        if (s.query != query) return@update s
                        s.copy(
                            isLoading = false,
                            isEnd = true,
                            errorMessage = if (page == 1) e.message ?: "Network error" else s.errorMessage,
                        )
                    }
                }
        }
    }
}
