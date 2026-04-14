const app = require('./app');
const { PORT } = require('./config/env');

app.listen(PORT, () => {
  console.log(`CineVault backend running on http://localhost:${PORT}`);
});