const express = require('express');
const {
  searchMovies,
  getMovieDetails,
} = require('../controllers/movieController');

const router = express.Router();

router.get('/movies/search', searchMovies);
router.get('/movies/:id', getMovieDetails);

module.exports = router;