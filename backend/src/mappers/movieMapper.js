const { TMDB_IMAGE_BASE_URL } = require('../config/env');

function mapTmdbMovieToSearchItem(movie, imdbId) {
  return {
    Title: movie.title || 'Unknown Title',
    Year: movie.release_date ? movie.release_date.slice(0, 4) : 'N/A',
    imdbID: imdbId,
    Type: 'movie',
    Poster: movie.poster_path
      ? `${TMDB_IMAGE_BASE_URL}${movie.poster_path}`
      : 'N/A',
  };
}

function mapMoviesToSearchResponse(movies) {
  if (!movies.length) {
    return {
      Search: [],
      totalResults: '0',
      Response: 'False',
      Error: 'No movies found',
    };
  }

  return {
    Search: movies,
    totalResults: String(movies.length),
    Response: 'True',
  };
}

module.exports = {
  mapTmdbMovieToSearchItem,
  mapMoviesToSearchResponse,
};