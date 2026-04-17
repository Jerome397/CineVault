package com.example.cinevault

import org.json.JSONObject
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

object BackendApi {

    fun searchMovies(query: String): List<MovieUIModel> {
        val encodedQuery = URLEncoder.encode(query, "UTF-8")
        val json = getJson("${Constants.BASE_URL}movies/search?query=$encodedQuery")
        return parseMovieList(json)
    }

    fun getPopularMovies(): List<MovieUIModel> {
        val json = getJson("${Constants.BASE_URL}movies/popular")
        return parseMovieList(json)
    }

    fun getTopRatedMovies(): List<MovieUIModel> {
        val json = getJson("${Constants.BASE_URL}movies/top-rated")
        return parseMovieList(json)
    }

    fun getMovieDetails(imdbId: String): MovieUIModel {
        val json = getJson("${Constants.BASE_URL}movies/$imdbId")

        if (json.optString("Response") == "False") {
            throw IOException(json.optString("Error", "Movie not found"))
        }

        val id = clean(json.optString("imdbID"))

        return MovieUIModel(
            id = id,
            title = clean(json.optString("Title")),
            year = clean(json.optString("Year")),
            posterUrl = cleanPoster(json.optString("Poster")),
            genre = clean(json.optString("Genre")),
            plot = clean(json.optString("Plot")),
            rating = clean(json.optString("imdbRating")),
            actors = clean(json.optString("Actors")),
            runtime = clean(json.optString("Runtime")),
            isFavorite = MovieStore.isFavorite(id)
        )
    }

    private fun parseMovieList(json: JSONObject): List<MovieUIModel> {
        if (json.optString("Response") == "False") {
            return emptyList()
        }

        val results = mutableListOf<MovieUIModel>()
        val searchArray = json.optJSONArray("Search") ?: return results

        for (i in 0 until searchArray.length()) {
            val item = searchArray.getJSONObject(i)
            val imdbId = clean(item.optString("imdbID"))

            results.add(
                MovieUIModel(
                    id = imdbId,
                    title = clean(item.optString("Title")),
                    year = clean(item.optString("Year")),
                    posterUrl = cleanPoster(item.optString("Poster")),
                    genre = clean(item.optString("Type")),
                    plot = "",
                    rating = "",
                    actors = "",
                    runtime = "",
                    isFavorite = MovieStore.isFavorite(imdbId)
                )
            )
        }

        return results
    }

    private fun getJson(urlString: String): JSONObject {
        val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
            requestMethod = "GET"
            connectTimeout = 15000
            readTimeout = 15000
        }

        return try {
            val responseCode = connection.responseCode
            val stream = if (responseCode in 200..299) {
                connection.inputStream
            } else {
                connection.errorStream
            }

            val body = stream.bufferedReader().use { it.readText() }

            if (body.isBlank()) {
                throw IOException("Empty response from backend")
            }

            JSONObject(body)
        } finally {
            connection.disconnect()
        }
    }

    private fun clean(value: String?): String {
        return if (value == null || value == "N/A") "" else value
    }

    private fun cleanPoster(value: String?): String? {
        return if (value == null || value == "N/A" || value.isBlank()) null else value
    }
}