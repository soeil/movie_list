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

// OMDB detail response: ?i=<imdbId> — all optional, API sends "N/A" for missing.
data class MovieDetail(
    val Title: String? = null,
    val Year: String? = null,
    val imdbID: String? = null,
    val Poster: String? = null,
    val Released: String? = null,
    val Runtime: String? = null,
    val Genre: String? = null,
    val Director: String? = null,
    val Writer: String? = null,
    val Actors: String? = null,
    val Plot: String? = null,
    val Language: String? = null,
    val Country: String? = null,
    val Awards: String? = null,
    val Response: String? = null,
    val Error: String? = null,
)
