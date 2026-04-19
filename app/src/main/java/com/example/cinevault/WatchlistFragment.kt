package com.example.cinevault

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WatchlistFragment : Fragment(R.layout.fragment_watchlist) {

    private lateinit var recyclerWatchlist: RecyclerView
    private lateinit var emptyState: View
    private lateinit var titleText: TextView
    private lateinit var viewModel: WatchlistViewModel

    private val watchlistAdapter = MovieRowAdapter(::showMovieDetails)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerWatchlist = view.findViewById(R.id.recyclerWatchlist)
        emptyState = view.findViewById(R.id.layoutEmptyState)
        titleText = view.findViewById(R.id.tvWatchlistTitle)

        recyclerWatchlist.layoutManager = LinearLayoutManager(requireContext())
        recyclerWatchlist.adapter = watchlistAdapter

        if (recyclerWatchlist.itemDecorationCount == 0) {
            recyclerWatchlist.addItemDecoration(SpacingItemDecoration(16))
        }

        val db = AppDatabase.getDatabase(requireContext())
        val repository = MovieRepository(
            RetrofitInstance.api,
            db.favoriteMovieDao(),
            db.watchlistMovieDao()
        )

        val factory = AppViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[WatchlistViewModel::class.java]

        viewModel.watchlist.observe(viewLifecycleOwner) { watchlist ->
            titleText.text = getString(R.string.my_watchlist_count, watchlist.size)

            if (watchlist.isEmpty()) {
                recyclerWatchlist.visibility = View.GONE
                emptyState.visibility = View.VISIBLE
            } else {
                emptyState.visibility = View.GONE
                recyclerWatchlist.visibility = View.VISIBLE

                val uiList = watchlist.map {
                    MovieUIModel(
                        id = it.imdbID,
                        title = it.title,
                        year = it.year,
                        posterUrl = it.poster,
                        genre = it.genre,
                        plot = it.plot,
                        rating = it.rating
                    )
                }

                watchlistAdapter.submitList(uiList)
            }
        }
    }

    private fun showMovieDetails(movie: MovieUIModel) {
        MovieDetailsBottomSheetFragment.newInstance(movie)
            .show(parentFragmentManager, MovieDetailsBottomSheetFragment.TAG)
    }
}