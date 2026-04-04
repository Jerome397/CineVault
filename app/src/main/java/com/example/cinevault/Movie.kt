package com.example.cinevault

data class Movie(
    val imdbID: String,
    val Title: String,
    val Year: String,
    val Poster: String = "N/A"
)