const PORT = process.env.PORT || 3000;
const NODE_ENV = process.env.NODE_ENV || 'development';
const OMDB_API_KEY = process.env.OMDB_API_KEY || '';
const OMDB_BASE_URL = process.env.OMDB_BASE_URL || 'https://www.omdbapi.com/';

module.exports = {
  PORT,
  NODE_ENV,
  OMDB_API_KEY,
  OMDB_BASE_URL,
};