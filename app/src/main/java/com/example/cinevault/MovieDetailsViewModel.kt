package com.example.cinevault

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class MovieDetailsViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _movieDetails = MutableLiveData<MovieDetails>()
    val movieDetails: LiveData<MovieDetails> = _movieDetails

    private val _isFavorite = MutableLiveData(false)
    val isFavorite: LiveData<Boolean> = _isFavorite

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun loadMovieDetails(imdbId: String) {
        viewModelScope.launch {
            try {
                val details = repository.getMovieDetails(imdbId)
                _movieDetails.value = details
                _isFavorite.value = repository.isFavorite(imdbId)
            } catch (e: Exception) {
                _error.value = e.message ?: "Failed to load movie details"
            }
        }
    }

    fun toggleFavorite() {
        val current = _movieDetails.value ?: return
        viewModelScope.launch {
            val currentlyFavorite = repository.isFavorite(current.imdbID)
            if (currentlyFavorite) {
                repository.removeFavorite(current)
                _isFavorite.value = false
            } else {
                repository.addFavorite(current)
                _isFavorite.value = true
            }
        }
    }
}