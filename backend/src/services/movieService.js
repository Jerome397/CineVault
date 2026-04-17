const omdbClient = require('../clients/omdbClient');
const tmdbClient = require('../clients/tmdbClient');
const {
  mapTmdbMovieToSearchItem,
  mapMoviesToSearchResponse,
} = require('../mappers/movieMapper');

async function searchMovies(query) {
  if (!query || !query.trim()) {
    const error = new Error('Query parameter is required');
    error.status = 400;
    throw error;
  }

  return omdbClient.searchMovies(query.trim());
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

async function enrichTmdbMovies(tmdbMovies) {
  const limitedMovies = tmdbMovies.slice(0, 10);
  console.log('[TMDB ENRICH] incoming movies:', limitedMovies.length);

  const enrichedMovies = await Promise.all(
    limitedMovies.map(async (movie) => {
      try {
        console.log('[TMDB ENRICH] checking movie:', movie.id, movie.title);

        const externalIds = await tmdbClient.getMovieExternalIds(movie.id);
        const imdbId = externalIds.imdb_id;

        console.log('[TMDB ENRICH] imdb_id:', movie.title, imdbId);

        if (!imdbId) {
          return null;
        }

        return mapTmdbMovieToSearchItem(movie, imdbId);
      } catch (error) {
        console.error('[TMDB ENRICH ERROR]', movie.id, movie.title, error.message);
        return null;
      }
    })
  );

  const filtered = enrichedMovies.filter(Boolean);
  console.log('[TMDB ENRICH] final mapped movies:', filtered.length);

  return filtered;
}

async function getPopularMovies() {
  const data = await tmdbClient.getPopularMovies();
  console.log('[POPULAR SERVICE] raw TMDb results:', (data.results || []).length);

  const movies = await enrichTmdbMovies(data.results || []);
  return mapMoviesToSearchResponse(movies);
}

async function getTopRatedMovies() {
  const data = await tmdbClient.getTopRatedMovies();
  console.log('[TOP RATED SERVICE] raw TMDb results:', (data.results || []).length);

  if (data.results && data.results.length) {
    console.log(
      '[TOP RATED SERVICE] first 3 titles:',
      data.results.slice(0, 3).map((m) => m.title)
    );
  }

  const movies = await enrichTmdbMovies(data.results || []);
  return mapMoviesToSearchResponse(movies);
}

module.exports = {
  searchMovies,
  getMovieDetails,
  getPopularMovies,
  getTopRatedMovies,
};