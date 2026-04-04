package com.example.cinevault

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData

class FavoritesViewModel(repository: MovieRepository) : ViewModel() {
    val favorites: LiveData<List<FavoriteMovieEntity>> = repository.getAllFavorites().asLiveData()
}