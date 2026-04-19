package com.example.cinevault

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class WatchlistViewModel(repository: MovieRepository) : ViewModel() {
    val watchlist: LiveData<List<WatchlistMovieEntity>> =
        repository.getAllWatchlist().asLiveData()
}