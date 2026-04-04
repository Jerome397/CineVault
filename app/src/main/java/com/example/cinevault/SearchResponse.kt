package com.example.cinevault

data class SearchResponse(
    val Search: List<Movie>? = emptyList(),
    val totalResults: String? = null,
    val Response: String = "False",
    val Error: String? = null
)