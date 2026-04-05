package com.example.cinevault

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var recyclerFavorites: RecyclerView
    private lateinit var emptyState: View
    private lateinit var titleText: TextView

    private val favoritesAdapter = MovieRowAdapter(::showMovieDetails)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerFavorites = view.findViewById(R.id.recyclerFavorites)
        emptyState = view.findViewById(R.id.layoutEmptyState)
        titleText = view.findViewById(R.id.tvFavoritesTitle)

        recyclerFavorites.layoutManager = LinearLayoutManager(requireContext())
        recyclerFavorites.adapter = favoritesAdapter

        if (recyclerFavorites.itemDecorationCount == 0) {
            recyclerFavorites.addItemDecoration(SpacingItemDecoration(16))
        }

        bindFavorites(DemoMovies.favorites)
    }

    private fun bindFavorites(favorites: List<MovieUIModel>) {
        titleText.text = getString(R.string.my_favorites_count, favorites.size)

        if (favorites.isEmpty()) {
            recyclerFavorites.visibility = View.GONE
            emptyState.visibility = View.VISIBLE
        } else {
            emptyState.visibility = View.GONE
            recyclerFavorites.visibility = View.VISIBLE
            favoritesAdapter.submitList(favorites)
        }
    }

    private fun showMovieDetails(movie: MovieUIModel) {
        MovieDetailsBottomSheetFragment.newInstance(movie)
            .show(parentFragmentManager, MovieDetailsBottomSheetFragment.TAG)
    }
}