const { OMDB_API_KEY, OMDB_BASE_URL } = require('../config/env');

function buildOmdbUrl(params = {}) {
  const url = new URL(OMDB_BASE_URL);

  url.searchParams.set('apikey', OMDB_API_KEY);

  Object.entries(params).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') {
      url.searchParams.set(key, value);
    }
  });

  return url.toString();
}

async function requestOmdb(params = {}) {
  if (!OMDB_API_KEY) {
    const error = new Error('OMDB_API_KEY is missing in .env');
    error.status = 500;
    throw error;
  }

  const url = buildOmdbUrl(params);
  const response = await fetch(url);

  if (!response.ok) {
    const error = new Error(`OMDb request failed with status ${response.status}`);
    error.status = response.status;
    throw error;
  }

  return response.json();
}

async function searchMovies(query) {
  return requestOmdb({ s: query });
}

async function getMovieDetails(imdbId) {
  return requestOmdb({ i: imdbId });
}

module.exports = {
  searchMovies,
  getMovieDetails,
};