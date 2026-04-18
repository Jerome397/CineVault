const movieService = require('../services/movieService');

async function searchMovies(req, res, next) {
  try {
    const { query } = req.query;
    console.log('[SEARCH REQUEST]', query);

    const data = await movieService.searchMovies(query);

    console.log(
      '[SEARCH RESPONSE]',
      data.Response,
      data.Search ? data.Search.length : 0
    );

    res.status(200).json(data);
  } catch (error) {
    console.error('[SEARCH ERROR]', error.message);
    next(error);
  }
}

async function getMovieDetails(req, res, next) {
  try {
    const { id } = req.params;
    console.log('[DETAILS REQUEST]', id);

    const data = await movieService.getMovieDetails(id);

    console.log('[DETAILS RESPONSE]', data.Title);

    res.status(200).json(data);
  } catch (error) {
    console.error('[DETAILS ERROR]', error.message);
    next(error);
  }
}

async function getPopularMovies(req, res, next) {
  try {
    console.log('[POPULAR REQUEST]');

    const data = await movieService.getPopularMovies();

    console.log(
      '[POPULAR RESPONSE]',
      data.Response,
      data.Search ? data.Search.length : 0
    );

    res.status(200).json(data);
  } catch (error) {
    console.error('[POPULAR ERROR]', error.message);
    next(error);
  }
}

async function getTopRatedMovies(req, res, next) {
  try {
    console.log('[TOP RATED REQUEST]');

    const data = await movieService.getTopRatedMovies();

    console.log(
      '[TOP RATED RESPONSE]',
      data.Response,
      data.Search ? data.Search.length : 0
    );

    res.status(200).json(data);
  } catch (error) {
    console.error('[TOP RATED ERROR]', error.message);
    next(error);
  }
}

module.exports = {
  searchMovies,
  getMovieDetails,
  getPopularMovies,
  getTopRatedMovies,
};