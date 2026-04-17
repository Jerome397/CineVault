const { TMDB_API_TOKEN, TMDB_BASE_URL } = require('../config/env');

async function requestTmdb(path, params = {}) {
  if (!TMDB_API_TOKEN) {
    const error = new Error('TMDB_API_TOKEN is missing in .env');
    error.status = 500;
    throw error;
  }

  const url = new URL(`${TMDB_BASE_URL}${path}`);

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      url.searchParams.set(key, value);
    }
  });

  const response = await fetch(url.toString(), {
    method: 'GET',
    headers: {
      Authorization: `Bearer ${TMDB_API_TOKEN}`,
      accept: 'application/json',
    },
  });

  if (!response.ok) {
    const error = new Error(`TMDb request failed with status ${response.status}`);
    error.status = response.status;
    throw error;
  }

  return response.json();
}

async function getPopularMovies(page = 1) {
  return requestTmdb('/movie/popular', { page });
}

async function getTopRatedMovies(page = 1) {
  return requestTmdb('/movie/top_rated', { page });
}

async function getMovieExternalIds(tmdbMovieId) {
  return requestTmdb(`/movie/${tmdbMovieId}/external_ids`);
}

module.exports = {
  getPopularMovies,
  getTopRatedMovies,
  getMovieExternalIds,
};