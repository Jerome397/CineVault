function getHealth(req, res) {
  res.status(200).json({
    success: true,
    message: 'CineVault backend is running',
  });
}

module.exports = {
  getHealth,
};