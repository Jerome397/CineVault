const express = require('express');

const healthRoutes = require('./routes/healthRoutes');
const movieRoutes = require('./routes/movieRoutes');
const notFound = require('./middlewares/notFound');
const errorHandler = require('./middlewares/errorHandler');

const app = express();

app.use(express.json());

app.use(healthRoutes);
app.use(movieRoutes);

app.use(notFound);
app.use(errorHandler);

module.exports = app;