package com.example.cinevault

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var featuredImage: ImageView
    private lateinit var featuredTitle: TextView
    private lateinit var featuredMeta: TextView
    private lateinit var featuredPlot: TextView
    private lateinit var buttonDetails: MaterialButton
    private lateinit var buttonFavorite: MaterialButton
    private lateinit var recyclerPopular: RecyclerView
    private lateinit var recyclerTopRated: RecyclerView
    private lateinit var contentContainer: View
    private lateinit var skeletonContainer: LinearLayout

    private val popularAdapter = MoviePosterAdapter(::showMovieDetails)
    private val topRatedAdapter = MoviePosterAdapter(::showMovieDetails)

    private var currentFeaturedMovie: MovieUIModel? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        featuredImage = view.findViewById(R.id.ivFeatured)
        featuredTitle = view.findViewById(R.id.tvFeaturedTitle)
        featuredMeta = view.findViewById(R.id.tvFeaturedMeta)
        featuredPlot = view.findViewById(R.id.tvFeaturedPlot)
        buttonDetails = view.findViewById(R.id.btnFeaturedDetails)
        buttonFavorite = view.findViewById(R.id.btnFeaturedFavorite)
        recyclerPopular = view.findViewById(R.id.recyclerPopular)
        recyclerTopRated = view.findViewById(R.id.recyclerTopRated)
        contentContainer = view.findViewById(R.id.contentContainer)
        skeletonContainer = view.findViewById(R.id.skeletonContainer)

        setupRecycler(recyclerPopular, popularAdapter)
        setupRecycler(recyclerTopRated, topRatedAdapter)

        buttonDetails.setOnClickListener {
            currentFeaturedMovie?.let(::showMovieDetails)
        }

        buttonFavorite.setOnClickListener {
            currentFeaturedMovie?.let { movie ->
                MovieStore.toggleFavorite(movie.id)
                updateFavoriteButton()
            }
        }

        renderFromStore()
    }

    override fun onResume() {
        super.onResume()
        renderFromStore()
    }

    private fun setupRecycler(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = adapter

        if (recyclerView.itemDecorationCount == 0) {
            recyclerView.addItemDecoration(SpacingItemDecoration(24, isHorizontal = true))
        }
    }

    private fun renderFromStore() {
        val movies = MovieStore.getAll().shuffled()

        skeletonContainer.visibility = View.GONE
        contentContainer.visibility = View.VISIBLE

        if (movies.isEmpty()) {
            currentFeaturedMovie = null

            featuredImage.setImageResource(R.drawable.poster_placeholder)
            featuredTitle.text = "No movies yet"
            featuredMeta.text = "Use Search to load movies"
            featuredPlot.text = "Once you search for a movie, it will appear here."
            buttonDetails.isEnabled = false
            buttonFavorite.isEnabled = false
            buttonFavorite.text = getString(R.string.add_to_favorites)

            popularAdapter.submitList(emptyList())
            topRatedAdapter.submitList(emptyList())
            return
        }

        val featured = movies.first()
        val remaining = movies.drop(1)
        val firstRow = remaining.take(10)
        val secondRow = remaining.drop(10).take(10)

        currentFeaturedMovie = featured

        featuredImage.load(featured.posterUrl) {
            crossfade(true)
            placeholder(R.drawable.poster_placeholder)
            error(R.drawable.poster_placeholder)
        }

        featuredTitle.text = featured.title
        featuredMeta.text = listOf(featured.year, featured.genre, "⭐ ${featured.rating.ifBlank { "N/A" }}")
            .filter { it.isNotBlank() }
            .joinToString(" • ")
        featuredPlot.text = featured.plot.ifBlank { "Open the movie to load full details." }

        buttonDetails.isEnabled = true
        buttonFavorite.isEnabled = true
        updateFavoriteButton()

        popularAdapter.submitList(firstRow)
        topRatedAdapter.submitList(secondRow)
    }

    private fun updateFavoriteButton() {
        val movie = currentFeaturedMovie ?: return
        buttonFavorite.text = if (MovieStore.isFavorite(movie.id)) {
            getString(R.string.added_to_favorites)
        } else {
            getString(R.string.add_to_favorites)
        }
    }

    private fun showMovieDetails(movie: MovieUIModel) {
        MovieDetailsBottomSheetFragment.newInstance(movie)
            .show(parentFragmentManager, MovieDetailsBottomSheetFragment.TAG)
    }
}