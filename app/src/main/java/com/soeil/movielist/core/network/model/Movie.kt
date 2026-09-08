package com.soeil.movielist.core.network.model

// OMDB search response: ?s=<query>&type=movie
data class MovieSearchResponse(
    val Search: List<Movie>? = null,
    val totalResults: String? = null,
    val Response: String,
    val Error: String? = null,
)

data class Movie(
    val Title: String,
    val Year: String,
    val imdbID: String,
    val Type: String,
    val Poster: String,
)
