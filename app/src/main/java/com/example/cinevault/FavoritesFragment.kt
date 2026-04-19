package com.example.cinevault

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class FavoritesFragment : Fragment(R.layout.fragment_favorites) {

    private lateinit var recyclerFavorites: RecyclerView
    private lateinit var emptyState: View
    private lateinit var titleText: TextView
    private lateinit var viewModel: FavoritesViewModel

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

        val db = AppDatabase.getDatabase(requireContext())
        val repository = MovieRepository(
            RetrofitInstance.api,
            db.favoriteMovieDao(),
            db.watchlistMovieDao()
        )
        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[FavoritesViewModel::class.java]

        viewModel.favorites.observe(viewLifecycleOwner) { favoriteEntities ->
            val favorites = favoriteEntities.map { entity ->
                MovieUIModel(
                    id = entity.imdbID,
                    title = entity.title,
                    year = entity.year,
                    posterUrl = entity.poster,
                    genre = entity.genre,
                    plot = entity.plot,
                    rating = entity.rating,
                    isFavorite = true
                )
            }

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
    }

    private fun showMovieDetails(movie: MovieUIModel) {
        MovieDetailsBottomSheetFragment.newInstance(movie)
            .show(parentFragmentManager, MovieDetailsBottomSheetFragment.TAG)
    }
}