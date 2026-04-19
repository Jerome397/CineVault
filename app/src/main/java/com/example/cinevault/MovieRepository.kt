package com.example.cinevault

class MovieRepository(
    private val api: MovieApiService,
    private val favoriteDao: FavoriteMovieDao,
    private val watchlistDao: WatchlistMovieDao
) {
    suspend fun searchMovies(query: String): SearchResponse = api.searchMovies(query)

    suspend fun getMovieDetails(imdbId: String): MovieDetails = api.getMovieDetails(imdbId)

    suspend fun addFavorite(details: MovieDetails) {
        favoriteDao.insert(
            FavoriteMovieEntity(
                imdbID = details.imdbID,
                title = details.Title,
                year = details.Year,
                poster = details.Poster,
                genre = details.Genre,
                rating = details.imdbRating,
                plot = details.Plot
            )
        )
    }

    suspend fun removeFavorite(details: MovieDetails) {
        favoriteDao.delete(
            FavoriteMovieEntity(
                imdbID = details.imdbID,
                title = details.Title,
                year = details.Year,
                poster = details.Poster,
                genre = details.Genre,
                rating = details.imdbRating,
                plot = details.Plot
            )
        )
    }

    fun getAllFavorites() = favoriteDao.getAllFavorites()

    suspend fun isFavorite(id: String): Boolean = favoriteDao.isFavorite(id)

    suspend fun addToWatchlist(details: MovieDetails) {
        watchlistDao.insert(
            WatchlistMovieEntity(
                imdbID = details.imdbID,
                title = details.Title,
                year = details.Year,
                poster = details.Poster,
                genre = details.Genre,
                rating = details.imdbRating,
                plot = details.Plot
            )
        )
    }

    suspend fun removeFromWatchlist(details: MovieDetails) {
        watchlistDao.delete(
            WatchlistMovieEntity(
                imdbID = details.imdbID,
                title = details.Title,
                year = details.Year,
                poster = details.Poster,
                genre = details.Genre,
                rating = details.imdbRating,
                plot = details.Plot
            )
        )
    }

    fun getAllWatchlist() = watchlistDao.getAllWatchlist()

    suspend fun isInWatchlist(id: String): Boolean = watchlistDao.isInWatchlist(id)
}