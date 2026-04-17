const omdbClient = require('../clients/omdbClient');

async function searchMovies(query) {
  if (!query || !query.trim()) {
    const error = new Error('Query parameter is required');
    error.status = 400;
    throw error;
  }

  const data = await omdbClient.searchMovies(query.trim());

  return data;
}

async function getMovieDetails(imdbId) {
  if (!imdbId || !imdbId.trim()) {
    const error = new Error('Movie id is required');
    error.status = 400;
    throw error;
  }

  const data = await omdbClient.getMovieDetails(imdbId.trim());

  if (data.Response === 'False') {
    const error = new Error(data.Error || 'Movie not found');
    error.status = 404;
    throw error;
  }

  return data;
}

module.exports = {
  searchMovies,
  getMovieDetails,
};