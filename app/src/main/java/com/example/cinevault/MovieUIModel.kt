package com.example.cinevault

import java.io.Serializable

data class MovieUIModel(
    val id: String,
    val title: String,
    val year: String,
    val posterUrl: String? = null,
    val genre: String = "",
    val plot: String = "",
    val rating: String = "",
    val actors: String = "",
    val runtime: String = "",
    val isFavorite: Boolean = false
) : Serializable