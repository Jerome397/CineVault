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

    private var featuredMovie: MovieUIModel? = null
    private var popularMovies: List<MovieUIModel> = emptyList()
    private var topRatedMovies: List<MovieUIModel> = emptyList()

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
            featuredMovie?.let(::showMovieDetails)
        }

        buttonFavorite.setOnClickListener {
            featuredMovie?.let { movie ->
                MovieStore.toggleFavorite(movie.id)
                updateFavoriteButton()
            }
        }

        loadHomeData()
    }

    override fun onResume() {
        super.onResume()
        updateFavoriteButton()
    }

    private fun setupRecycler(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = adapter

        if (recyclerView.itemDecorationCount == 0) {
            recyclerView.addItemDecoration(SpacingItemDecoration(24, isHorizontal = true))
        }
    }

    private fun loadHomeData() {
        skeletonContainer.visibility = View.VISIBLE
        contentContainer.visibility = View.INVISIBLE

        Thread {
            try {
                val popular = BackendApi.getPopularMovies()
                val topRated = BackendApi.getTopRatedMovies()

                MovieStore.upsertMovies(popular)
                MovieStore.upsertMovies(topRated)

                activity?.runOnUiThread {
                    if (!isAdded) return@runOnUiThread
                    bindHomeData(popular, topRated)
                }
            } catch (_: Exception) {
                activity?.runOnUiThread {
                    if (!isAdded) return@runOnUiThread
                    bindHomeData(emptyList(), emptyList())
                }
            }
        }.start()
    }

    private fun bindHomeData(popular: List<MovieUIModel>, topRated: List<MovieUIModel>) {
        popularMovies = popular
        topRatedMovies = topRated
        featuredMovie = popular.firstOrNull()

        skeletonContainer.visibility = View.GONE
        contentContainer.visibility = View.VISIBLE

        val featured = featuredMovie

        if (featured == null) {
            featuredImage.setImageResource(R.drawable.poster_placeholder)
            featuredTitle.text = "No movies available"
            featuredMeta.text = "Popular movies will appear here"
            featuredPlot.text = ""
            buttonDetails.isEnabled = false
            buttonFavorite.isEnabled = false
            buttonFavorite.text = getString(R.string.add_to_favorites)

            popularAdapter.submitList(emptyList())
            topRatedAdapter.submitList(topRatedMovies)
            return
        }

        featuredImage.load(featured.posterUrl) {
            crossfade(true)
            placeholder(R.drawable.poster_placeholder)
            error(R.drawable.poster_placeholder)
        }

        featuredTitle.text = featured.title
        featuredMeta.text = listOf(featured.year, featured.genre)
            .filter { it.isNotBlank() }
            .joinToString(" • ")
        featuredPlot.text = ""

        buttonDetails.isEnabled = true
        buttonFavorite.isEnabled = true
        updateFavoriteButton()

        popularAdapter.submitList(popularMovies.drop(1))
        topRatedAdapter.submitList(topRatedMovies)
    }

    private fun updateFavoriteButton() {
        val movie = featuredMovie
        if (movie == null) {
            buttonFavorite.isEnabled = false
            buttonFavorite.text = getString(R.string.add_to_favorites)
            return
        }

        buttonFavorite.isEnabled = true
        buttonFavorite.text = if (MovieStore.isFavorite(movie.id)) {
            getString(R.string.added_to_favorites)
        } else {
            getString(R.string.add_to_favorites)
        }
    }

    private fun showMovieDetails(movie: MovieUIModel) {
        MovieStore.upsertMovie(movie)
        MovieDetailsBottomSheetFragment.newInstance(movie)
            .show(parentFragmentManager, MovieDetailsBottomSheetFragment.TAG)
    }
}