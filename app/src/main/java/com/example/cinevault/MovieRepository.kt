package com.example.cinevault

class MovieRepository(
    private val api: MovieApiService,
    private val favoriteDao: FavoriteMovieDao,
    private val watchlistDao: WatchlistMovieDao,
    private val cachedMovieDao: CachedMovieDao
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

    suspend fun cachePopularMovies(movies: List<MovieUIModel>) {
        cachedMovieDao.clearCategory("popular")
        cachedMovieDao.insertAll(
            movies.map {
                CachedMovieEntity(
                    imdbID = it.id,
                    title = it.title,
                    year = it.year,
                    poster = it.posterUrl ?: "",
                    genre = it.genre,
                    plot = it.plot,
                    rating = it.rating,
                    category = "popular"
                )
            }
        )
    }

    suspend fun cacheTopRatedMovies(movies: List<MovieUIModel>) {
        cachedMovieDao.clearCategory("top_rated")
        cachedMovieDao.insertAll(
            movies.map {
                CachedMovieEntity(
                    imdbID = it.id,
                    title = it.title,
                    year = it.year,
                    poster = it.posterUrl ?: "",
                    genre = it.genre,
                    plot = it.plot,
                    rating = it.rating,
                    category = "top_rated"
                )
            }
        )
    }

    fun getCachedPopularMovies() = cachedMovieDao.getMoviesByCategory("popular")

    fun getCachedTopRatedMovies() = cachedMovieDao.getMoviesByCategory("top_rated")
}