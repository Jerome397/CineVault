function getHealth(req, res) {
  console.log('[HEALTH REQUEST]');
  
  res.status(200).json({
    success: true,
    message: 'CineVault backend is running',
  });
}

module.exports = {
  getHealth,
};