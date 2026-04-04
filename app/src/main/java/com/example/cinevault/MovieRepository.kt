package com.example.cinevault

class MovieRepository(
    private val api: MovieApiService,
    private val dao: FavoriteMovieDao
) {
    suspend fun searchMovies(query: String): SearchResponse = api.searchMovies(query)

    suspend fun getMovieDetails(imdbId: String): MovieDetails = api.getMovieDetails(imdbId)

    suspend fun addFavorite(details: MovieDetails) {
        dao.insert(
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
        dao.delete(
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

    fun getAllFavorites() = dao.getAllFavorites()

    suspend fun isFavorite(id: String): Boolean = dao.isFavorite(id)
}