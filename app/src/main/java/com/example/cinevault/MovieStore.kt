package com.example.cinevault

object MovieStore {

    private val movies = linkedMapOf<String, MovieUIModel>()

    private val favoriteIds = mutableSetOf<String>()
    private val watchlistIds = mutableSetOf<String>()

    private fun applyState(movie: MovieUIModel): MovieUIModel {
        return movie.copy(
            isFavorite = favoriteIds.contains(movie.id)
        )
    }

    fun upsertMovie(movie: MovieUIModel) {
        movies[movie.id] = applyState(movie)
    }

    fun upsertMovies(newMovies: List<MovieUIModel>) {
        newMovies.forEach { upsertMovie(it) }
    }

    fun getAll(): List<MovieUIModel> {
        return movies.values.map { applyState(it) }
    }

    fun getPopular(): List<MovieUIModel> {
        return getAll()
    }

    fun getTopRated(): List<MovieUIModel> {
        return getAll()
    }

    fun getFavorites(): List<MovieUIModel> {
        return favoriteIds.mapNotNull { id -> movies[id] }.map { applyState(it) }
    }

    fun getWatchlist(): List<MovieUIModel> {
        return watchlistIds.mapNotNull { id -> movies[id] }.map { applyState(it) }
    }

    fun getMovieById(id: String): MovieUIModel? {
        return movies[id]?.let { applyState(it) }
    }

    fun isFavorite(id: String): Boolean {
        return favoriteIds.contains(id)
    }

    fun isInWatchlist(id: String): Boolean {
        return watchlistIds.contains(id)
    }

    fun toggleFavorite(id: String): Boolean {
        if (!movies.containsKey(id)) return false

        return if (favoriteIds.contains(id)) {
            favoriteIds.remove(id)
            false
        } else {
            favoriteIds.add(id)
            true
        }
    }

    fun toggleWatchlist(id: String): Boolean {
        if (!movies.containsKey(id)) return false

        return if (watchlistIds.contains(id)) {
            watchlistIds.remove(id)
            false
        } else {
            watchlistIds.add(id)
            true
        }
    }
}