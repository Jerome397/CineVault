package com.example.cinevault

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieApiService {
    @GET("movies/search")
    suspend fun searchMovies(@Query("query") query: String): SearchResponse

    @GET("movies/{id}")
    suspend fun getMovieDetails(@Path("id") imdbId: String): MovieDetails
}