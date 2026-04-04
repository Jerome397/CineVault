package com.example.cinevault

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: MovieRepository) : ViewModel() {

    private val _movies = MutableLiveData<List<Movie>>()
    val movies: LiveData<List<Movie>> = _movies

    private val _loading = MutableLiveData(false)
    val loading: LiveData<Boolean> = _loading

    private val _error = MutableLiveData<String?>()
    val error: LiveData<String?> = _error

    fun searchMovies(query: String) {
        if (query.isBlank()) {
            _error.value = "Enter a movie name"
            return
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.searchMovies(query)
                if (response.Response == "True") {
                    _movies.value = response.Search ?: emptyList()
                } else {
                    _movies.value = emptyList()
                    _error.value = response.Error ?: "No results found"
                }
            } catch (e: Exception) {
                _movies.value = emptyList()
                _error.value = e.message ?: "Search failed"
            } finally {
                _loading.value = false
            }
        }
    }
}