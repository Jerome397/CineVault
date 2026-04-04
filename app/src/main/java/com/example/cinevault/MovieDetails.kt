package com.example.cinevault

data class MovieDetails(
    val imdbID: String,
    val Title: String,
    val Year: String,
    val Poster: String = "N/A",
    val Genre: String = "Unknown",
    val Plot: String = "No plot available.",
    val imdbRating: String = "N/A"
)