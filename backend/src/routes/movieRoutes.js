const express = require('express');
const {
  searchMovies,
  getMovieDetails,
  getPopularMovies,
  getTopRatedMovies,
} = require('../controllers/movieController');

const router = express.Router();

router.get('/movies/search', searchMovies);
router.get('/movies/popular', getPopularMovies);
router.get('/movies/top-rated', getTopRatedMovies);
router.get('/movies/:id', getMovieDetails);

module.exports = router;