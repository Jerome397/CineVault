package com.example.cinevault

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cached_movies")
data class CachedMovieEntity(
    @PrimaryKey val imdbID: String,
    val title: String,
    val year: String,
    val poster: String,
    val genre: String,
    val plot: String,
    val rating: String,
    val category: String
)