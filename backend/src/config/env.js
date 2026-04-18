const PORT = process.env.PORT || 3000;
const NODE_ENV = process.env.NODE_ENV || 'development';

const OMDB_API_KEY = process.env.OMDB_API_KEY || '';
const OMDB_BASE_URL = process.env.OMDB_BASE_URL || 'https://www.omdbapi.com/';

const TMDB_API_TOKEN =process.env.TMDB_API_TOKEN || '';
const TMDB_BASE_URL = process.env.TMDB_BASE_URL || 'https://api.themoviedb.org/3';
const TMDB_IMAGE_BASE_URL = process.env.TMDB_IMAGE_BASE_URL || 'https://image.tmdb.org/t/p/w500';

module.exports = {
  PORT,
  NODE_ENV,
  OMDB_API_KEY,
  OMDB_BASE_URL,
  TMDB_API_TOKEN,
  TMDB_BASE_URL,
  TMDB_IMAGE_BASE_URL,
};