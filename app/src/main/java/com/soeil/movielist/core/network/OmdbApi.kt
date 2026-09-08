package com.soeil.movielist.core.network

import com.soeil.movielist.core.network.model.MovieDetail
import com.soeil.movielist.core.network.model.MovieSearchResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface OmdbApi {
    @GET("/")
    suspend fun searchMovies(
        @Query("s") query: String,
        @Query("type") type: String = "movie",
        @Query("page") page: Int = 1,
    ): MovieSearchResponse

    @GET("/")
    suspend fun getMovie(@Query("i") imdbId: String): MovieDetail
}
