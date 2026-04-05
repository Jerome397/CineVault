package com.example.cinevault

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.button.MaterialButton

class HomeFragment : Fragment(R.layout.fragment_home) {

    private var skeletonAnimator: ObjectAnimator? = null

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

    private lateinit var currentFeaturedMovie: MovieUIModel

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
        startLoadingState()

        val shuffledMovies = DemoMovies.all.shuffled()
        currentFeaturedMovie = shuffledMovies.first()
        val exploreMovies = shuffledMovies.drop(1)
        val topMovies = DemoMovies.topRated.shuffled()

        buttonDetails.setOnClickListener { showMovieDetails(currentFeaturedMovie) }

        buttonFavorite.setOnClickListener {
            pulseView(buttonFavorite)
            buttonFavorite.text = getString(R.string.added_to_favorites)
        }

        view.postDelayed({
            bindFeaturedMovie(currentFeaturedMovie)
            popularAdapter.submitList(exploreMovies)
            topRatedAdapter.submitList(topMovies)
            endLoadingState()
        }, 850)
    }

    private fun setupRecycler(recyclerView: RecyclerView, adapter: RecyclerView.Adapter<*>) {
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false)
        recyclerView.adapter = adapter

        if (recyclerView.itemDecorationCount == 0) {
            recyclerView.addItemDecoration(SpacingItemDecoration(24, isHorizontal = true))
        }
    }

    private fun bindFeaturedMovie(movie: MovieUIModel) {
        featuredImage.load(movie.posterUrl) {
            crossfade(true)
            placeholder(R.drawable.poster_placeholder)
            error(R.drawable.poster_placeholder)
        }

        featuredTitle.text = movie.title
        featuredMeta.text = listOf(movie.year, movie.genre, "⭐ ${movie.rating}")
            .filter { it.isNotBlank() }
            .joinToString(" • ")
        featuredPlot.text = movie.plot
    }

    private fun startLoadingState() {
        contentContainer.visibility = View.INVISIBLE
        skeletonContainer.visibility = View.VISIBLE

        skeletonAnimator?.cancel()
        skeletonAnimator = ObjectAnimator.ofFloat(skeletonContainer, View.ALPHA, 0.45f, 1f).apply {
            duration = 900
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
            interpolator = AccelerateDecelerateInterpolator()
            start()
        }
    }

    private fun endLoadingState() {
        skeletonAnimator?.cancel()
        skeletonAnimator = null
        skeletonContainer.visibility = View.GONE
        contentContainer.alpha = 0f
        contentContainer.visibility = View.VISIBLE
        contentContainer.animate().alpha(1f).setDuration(250).start()
    }

    private fun showMovieDetails(movie: MovieUIModel) {
        MovieDetailsBottomSheetFragment.newInstance(movie)
            .show(parentFragmentManager, MovieDetailsBottomSheetFragment.TAG)
    }

    private fun pulseView(view: View) {
        view.animate()
            .scaleX(1.08f)
            .scaleY(1.08f)
            .setDuration(100)
            .withEndAction {
                view.animate()
                    .scaleX(1f)
                    .scaleY(1f)
                    .setDuration(100)
                    .start()
            }
            .start()
    }

    override fun onDestroyView() {
        skeletonAnimator?.cancel()
        skeletonAnimator = null
        super.onDestroyView()
    }
}