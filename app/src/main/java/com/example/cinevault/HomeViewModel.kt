package com.example.cinevault

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class HomeViewModel(repository: MovieRepository) : ViewModel() {
    val cachedPopularMovies: LiveData<List<CachedMovieEntity>> =
        repository.getCachedPopularMovies().asLiveData()

    val cachedTopRatedMovies: LiveData<List<CachedMovieEntity>> =
        repository.getCachedTopRatedMovies().asLiveData()
}