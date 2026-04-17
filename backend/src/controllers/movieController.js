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

module.exports = {
  searchMovies,
  getMovieDetails,
};